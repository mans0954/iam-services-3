package org.openiam.idm.srvc.pswd.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.service.OrganizationDAO;
import org.openiam.idm.srvc.policy.domain.PolicyObjectAssocEntity;
import org.openiam.idm.srvc.policy.dto.PasswordPolicyAssocSearchBean;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyObjectAssoc;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.idm.srvc.policy.service.PolicyObjectAssocDAO;
import org.openiam.idm.srvc.policy.service.PolicyService;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
    private PolicyService policyDataService;
    @Autowired
    protected OrganizationDAO organizationDAO;
    @Autowired
    protected org.openiam.idm.srvc.role.service.RoleDAO roleDAO;
    @Autowired
    protected UserDAO userDAO;


    @Override
    public Policy getPasswordPolicyByUser(PasswordPolicyAssocSearchBean searchBean) {
        UserEntity user = null;
        if(StringUtils.isNotBlank(searchBean.getUserId())){
            user = userDAO.findById(searchBean.getUserId());
        }
        if(user != null) {
            return getPasswordPolicyByUser(user, searchBean.getManagedSystemId());
        } else {
            log.info("User can't be found. Using global association password policy.");
            // did not find anything - get the global policy
            return getGlobalPasswordPolicy();
        }
    }

    @Override
    public Policy getPasswordPolicyByUser(UserEntity user, String managedSystemId) {
        // Find a password policy for this user
        // order of search, type, classification, domain, global

        PolicyObjectAssoc policyAssocEntity = null;

        log.info("Looking for associate by managedSystemId.");
        if (StringUtils.isNotBlank(managedSystemId)) {
            policyAssocEntity = policyDataService.findAssociationByLevel(
                    "MANAGED_SYSTEM", managedSystemId);
            log.info(String.format("Association found: %s", policyAssocEntity));
            if (policyAssocEntity != null) {
                return getPolicy(policyAssocEntity);
            }
        }

        log.info("Looking for associate by metadata type.");
        log.info(String.format("User type =%s", user.getId()));
        if (user.getType() != null) {
            policyAssocEntity = policyDataService.findAssociationByLevel(
                    "USER_TYPE", user.getType().getId());
            log.info(String.format("Association found: %s", policyAssocEntity));
            if (policyAssocEntity != null) {
                return getPolicy(policyAssocEntity);
            }
        }


        if (user.getId() != null) {
            //  set by ORGANIZATION
            List<OrganizationEntity> orgEntity = organizationDAO
                    .getOrganizationsForUser(user.getId(), null, -1, -1);
            if (CollectionUtils.isNotEmpty(orgEntity)) {
                for (OrganizationEntity organization : orgEntity) {
                    log.info("Looking for associate by organization.");
                    policyAssocEntity = policyDataService
                            .findAssociationByLevel("ORGANIZATION",
                                    organization.getId());
                    log.info(String.format("Association found: %s", policyAssocEntity));
                    if (policyAssocEntity != null) {
                        log.info("PolicyAssoc found=" + policyAssocEntity);
                        break;
                    }
                }
                if (policyAssocEntity != null) {
                    return getPolicy(policyAssocEntity);
                }
            }
            //  set by ROLES
            List<RoleEntity> roles = roleDAO.getRolesForUser(user.getId(), null, -1, -1);
            if (CollectionUtils.isNotEmpty(roles)) {
                for (RoleEntity role : roles) {
                    log.info("Looking for associate by roles.");
                    policyAssocEntity = policyDataService
                            .findAssociationByLevel("ROLE",
                                    role.getId());
                    log.info(String.format("Association found: %s", policyAssocEntity));
                    if (policyAssocEntity != null) {
                        log.info("PolicyAssoc found=" + policyAssocEntity);
                        break;
                    }
                }
                if (policyAssocEntity != null) {
                    return getPolicy(policyAssocEntity);
                }
            }
        }

        log.info("Using global association password policy.");
        // did not find anything - get the global policy
        return getGlobalPasswordPolicy();
    }

    private Policy getPolicy(PolicyObjectAssoc policyAssoc) {
        log.info("Retreiving policyId=" + policyAssoc.getPolicyId());
        return policyDataService.getPolicy(policyAssoc.getPolicyId());
    }


    /**
     * Returns the global password policy
     *
     * @return
     */
    @Override
    public Policy getGlobalPasswordPolicy() {
        log.info("Fetching global association password policy.");
        PolicyObjectAssoc policyAssocEntity = policyDataService.findAssociationByLevel("GLOBAL", "GLOBAL");
        log.info(String.format("Association found: %s", policyAssocEntity));
        if (policyAssocEntity == null) {
            return null;
        }
        return getPolicy(policyAssocEntity);
    }
}
