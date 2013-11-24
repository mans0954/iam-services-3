package org.openiam.idm.srvc.pswd.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.service.OrganizationDAO;
import org.openiam.idm.srvc.policy.domain.PolicyObjectAssocEntity;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.service.PolicyDAO;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.idm.srvc.policy.service.PolicyObjectAssocDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private PolicyObjectAssocDAO policyObjectAssocDao;
    @Autowired
    private PolicyDataService policyDataService;
    @Autowired
    protected OrganizationDAO organizationDAO;
    @Autowired
    protected UserDataService userManager;



    public Policy getPasswordPolicyByUser(String domainId, String userId){
        return getPasswordPolicyByUser(domainId, userManager.getUser(userId));
    }

    public Policy getPasswordPolicyByUser(String domainId, UserEntity user){
        // Find a password policy for this user
        // order of search, type, classification, domain, global

        PolicyObjectAssocEntity policyAssocEntity = null;

        log.info(String.format("User type and classifcation=%s %s", user.getUserId(), user.getUserTypeInd()));

        if (user.getClassification() != null) {
            log.info("Looking for associate by classification.");
            policyAssocEntity = policyObjectAssocDao.findAssociationByLevel(
                    "CLASSIFICATION", user.getClassification());
            log.info(String.format("Association found: %s", policyAssocEntity));
            if (policyAssocEntity != null) {
                return getPolicy(policyAssocEntity);
            }
        }

        // look to see if a policy exists for the type of user
        if (user.getUserTypeInd() != null) {
            log.info("Looking for associate by type.");
            policyAssocEntity = policyObjectAssocDao.findAssociationByLevel(
                    "TYPE", user.getUserTypeInd());
            log.info(String.format("Association found: %s", policyAssocEntity));
            if (policyAssocEntity != null) {
                return getPolicy(policyAssocEntity);
            }
        }

        if (domainId != null) {
            log.info("Looking for associate by domain.");
            policyAssocEntity = policyObjectAssocDao.findAssociationByLevel(
                    "DOMAIN", domainId);
            log.info(String.format("Association found: %s", policyAssocEntity));
            if (policyAssocEntity != null) {
                return getPolicy(policyAssocEntity);
            }
        }
        //  set by ORGANIZATION

        if (user.getUserId() != null) {

            List<OrganizationEntity> orgEntity = organizationDAO
                    .getOrganizationsForUser(user.getUserId(), null, 0, 10);

            for (OrganizationEntity organization : orgEntity) {

                log.info("Looking for associate by organization.");
                policyAssocEntity = policyObjectAssocDao
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

        log.info("Using global association password policy.");
        // did not find anything - get the global policy
        return getGlobalPasswordPolicy();
    }

    private Policy getPolicy(PolicyObjectAssocEntity policyAssoc) {
        log.info("Retreiving policyId=" + policyAssoc.getPolicyId());
        return policyDataService.getPolicy(policyAssoc.getPolicyId());
    }


    /**
     * Returns the global password policy
     *
     * @return
     */
    public Policy getGlobalPasswordPolicy() {
        log.info("Fetching global association password policy.");
        PolicyObjectAssocEntity policyAssocEntity = policyObjectAssocDao
                .findAssociationByLevel("GLOBAL", "GLOBAL");
        log.info(String.format("Association found: %s", policyAssocEntity));
        if (policyAssocEntity == null) {
            return null;
        }
        return getPolicy(policyAssocEntity);
    }
}
