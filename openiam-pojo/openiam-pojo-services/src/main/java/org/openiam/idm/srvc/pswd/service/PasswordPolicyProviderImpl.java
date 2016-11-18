package org.openiam.idm.srvc.pswd.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.am.srvc.dao.ContentProviderDao;
import org.openiam.am.srvc.dao.URIPatternDao;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.am.srvc.dto.jdbc.AbstractAuthorizationPolicyEntity;
import org.openiam.am.srvc.dto.jdbc.AuthorizationGroup;
import org.openiam.am.srvc.dto.jdbc.AuthorizationRole;
import org.openiam.am.srvc.dto.jdbc.GroupAuthorizationRight;
import org.openiam.am.srvc.dto.jdbc.OrganizationAuthorizationRight;
import org.openiam.am.srvc.dto.jdbc.RoleAuthorizationRight;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.SysConfiguration;
import org.openiam.dozer.converter.PolicyDozerConverter;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.org.service.OrganizationDAO;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.dto.PasswordPolicyAssocSearchBean;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.service.PolicyDAO;
import org.openiam.idm.srvc.policy.service.PolicyService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 10/3/13
 * Time: 11:21 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class PasswordPolicyProviderImpl implements PasswordPolicyProvider {
    private static final Log log = LogFactory.getLog(PasswordServiceImpl.class);

    @Autowired
    private ContentProviderDao contentProviderDAO;
    @Autowired
    protected OrganizationDAO organizationDAO;
    @Autowired
    protected UserDataService userManager;
    
    @Autowired
    private PolicyDAO policyDAO;
    @Autowired
    private AuthProviderDao authProviderDAO;

    @Autowired
    protected org.openiam.idm.srvc.role.service.RoleDAO roleDAO;
    @Autowired
    protected UserDAO userDAO;

    @Autowired
    private SysConfiguration sysConfiguration;
    
    @Autowired
    private PolicyDozerConverter policyDozerConverter;
    
    @Autowired
    private URIPatternDao uriPatternDAO;
    
    @Autowired
    private AuthorizationManagerService authManager;
    
    @Autowired
    private DefaultPasswordPolicyResolver policyResolver;
    
    @Autowired
    private LoginDAO loginDAO;
    
    @Autowired
    private ManagedSysDAO managedSysDAO;
    
    @PostConstruct
    public void init() {
    	
    }
    
    @Override
    @Transactional
    public Policy getGlobalPasswordPolicy() {
        Policy retVal = null;
        final AuthProviderEntity authProvider = authProviderDAO.findById(sysConfiguration.getDefaultAuthProviderId());
        if(authProvider != null) {
            final PolicyEntity policyEntity = authProvider.getPasswordPolicy();
            if(policyEntity != null) {
                retVal = policyDozerConverter.convertToDTO(policyEntity, true);
            }
        }
        return retVal;
    }

    @Override
    @Transactional
    public Policy getPasswordPolicy(final PasswordPolicyAssocSearchBean searchBean) {
    	final List<PolicyEntity> policyEntities = new LinkedList<PolicyEntity>();
    	final PolicyEntity cpEntity = getPasswordPolicyForContentProvider(searchBean.getContentProviderId(), searchBean.getPatternId());
    	String userId = searchBean.getUserId();
    	if(StringUtils.isBlank(userId) && StringUtils.isNotBlank(searchBean.getPrincipal()) && StringUtils.isNotBlank(searchBean.getManagedSysId())) {
    		final LoginEntity login = loginDAO.getRecord(searchBean.getPrincipal(), searchBean.getManagedSysId());
    		if(login != null) {
    			userId = login.getUserId();
    		}
    	}
    	PolicyEntity managedSysgtemPolicy = null;
    	if(StringUtils.isNotBlank(searchBean.getManagedSysId())) {
    		final ManagedSysEntity mSys = managedSysDAO.findById(searchBean.getManagedSysId());
    		if(mSys != null) {
    			managedSysgtemPolicy = mSys.getPolicy();
    		}
    	}
        if(StringUtils.isNotBlank(searchBean.getUserId())) {
        	final List<AbstractAuthorizationPolicyEntity> policies = new LinkedList<AbstractAuthorizationPolicyEntity>();
        	final Set<RoleAuthorizationRight> roles = authManager.getRolesForUser(searchBean.getUserId());
        	if(CollectionUtils.isNotEmpty(roles)) {
        		policies.addAll(roles.stream().map(e -> e.getEntity()).collect(Collectors.toList()));
        	}
        			
        	final Set<GroupAuthorizationRight> groups = authManager.getGroupsForUser(searchBean.getUserId());
        	if(CollectionUtils.isNotEmpty(groups)) {
        		policies.addAll(groups.stream().map(e -> e.getEntity()).collect(Collectors.toList()));
        	}
        	
        	final Set<OrganizationAuthorizationRight> organizations = authManager.getOrganizationsForUser(searchBean.getUserId());
        	if(CollectionUtils.isNotEmpty(organizations)) {
        		policies.addAll(organizations.stream().map(e -> e.getEntity()).collect(Collectors.toList()));
        	}
        	
        	policyEntities.addAll(policies.stream().map(e -> policyDAO.findById(e.getPasswordPolicyId())).collect(Collectors.toList()));
        }
        if(cpEntity != null) {
        	policyEntities.add(cpEntity);
        }
        if(managedSysgtemPolicy != null) {
        	policyEntities.add(managedSysgtemPolicy);
        }
        
        final PolicyEntity policy = policyResolver.resolve(searchBean, policyEntities);
        if(policy == null) {
        	return getGlobalPasswordPolicy();
        } else {
        	return policyDozerConverter.convertToDTO(policy, true);
        }
    }

    private PolicyEntity getPasswordPolicyForContentProvider(final String contentProviderId, final String patternId) {
    	PolicyEntity retVal = null;
        if(StringUtils.isNotBlank(patternId)) {
        	final URIPatternEntity pattern = uriPatternDAO.findById(patternId);
        	if(pattern != null) {
        		retVal = getPolicyFromAuthProvider(pattern.getAuthProvider());
        	}
        }
        if(retVal == null && StringUtils.isNotBlank(contentProviderId)) {
            final ContentProviderEntity contentProvider = contentProviderDAO.findById(contentProviderId);
            if(contentProvider != null) {
            	retVal = getPolicyFromAuthProvider(contentProvider.getAuthProvider());
            }
        }
        //if(retVal == null) {
        //    retVal = getGlobalPasswordPolicy();
        //}
        return retVal;
    }
    
    private PolicyEntity getPolicyFromAuthProvider(final AuthProviderEntity authProvider) {
    	PolicyEntity retVal = null;
    	if(authProvider != null) {
    		retVal = authProvider.getPasswordPolicy();
        }
    	return retVal;
    }
}
