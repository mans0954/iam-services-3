package org.openiam.idm.srvc.pswd.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.am.srvc.dao.ContentProviderDao;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.base.SysConfiguration;
import org.openiam.dozer.converter.PolicyDozerConverter;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.service.OrganizationDAO;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.dto.PasswordPolicyAssocSearchBean;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private AuthProviderDao authProviderDAO;

    @Autowired
    private SysConfiguration sysConfiguration;
    
    @Autowired
    private PolicyDozerConverter policyDozerConverter;

    @Override
    @Transactional
    public Policy getGlobalPasswordPolicy() {
    	Policy retVal = null;
    	final AuthProviderEntity authProvider = authProviderDAO.findById(sysConfiguration.getDefaultAuthProviderId());
    	if(authProvider != null) {
    		final PolicyEntity policyEntity = authProvider.getPolicy();
    		if(policyEntity != null) {
    			retVal = policyDozerConverter.convertToDTO(policyEntity, true);
    		}
    	}
    	return retVal;
    }
    
    @Override
    @Transactional
    public Policy getPasswordPolicyByUser(final PasswordPolicyAssocSearchBean searchBean) {
    	return getPasswordPolicyByUser(userManager.getUser(searchBean.getUserId()), searchBean.getContentProviderId());
    }
    
	private Policy getPasswordPolicyByUser(final UserEntity user, final String contentProviderId) {
		Policy retVal = null;
		if(StringUtils.isNotBlank(contentProviderId)) {
			final ContentProviderEntity contentProvider = contentProviderDAO.findById(contentProviderId);
			if(contentProvider != null) {
				final AuthProviderEntity authProvider = contentProvider.getAuthProvider();
				if(authProvider != null) {
					final PolicyEntity policyEntity = authProvider.getPolicy();
					if(policyEntity != null) {
						retVal = policyDozerConverter.convertToDTO(policyEntity, true);
					}
				}
			}
		}
		if(retVal == null) {
			retVal = getGlobalPasswordPolicy();
		}
		return retVal;
	}
}
