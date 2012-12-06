/*
 * Copyright 2009, OpenIAM LLC This file is part of the OpenIAM Identity and
 * Access Management Suite
 * 
 * OpenIAM Identity and Access Management Suite is free software: you can
 * redistribute it and/or modify it under the terms of the Lesser GNU General
 * Public License version 3 as published by the Free Software Foundation.
 * 
 * OpenIAM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the Lesser GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenIAM. If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.idm.srvc.policy.service;

import java.util.ArrayList;
import java.util.List;
import javax.jws.WebService;
import org.apache.commons.collections.CollectionUtils;
import org.openiam.dozer.converter.PolicyAttributeDozerConverter;
import org.openiam.dozer.converter.PolicyDefDozerConverter;
import org.openiam.dozer.converter.PolicyDefParamDozerConverter;
import org.openiam.dozer.converter.PolicyDozerConverter;
import org.openiam.dozer.converter.PolicyObjectAssocDozerConverter;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyDef;
import org.openiam.idm.srvc.policy.dto.PolicyDefParam;
import org.openiam.idm.srvc.policy.dto.PolicyObjectAssoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * PolicyDataService is used create and manage policies. 
 * Enforcement of these policies is handled through policy specific services and policy enforcement points. 
 * @author suneet
 *
 */
@WebService(endpointInterface = "org.openiam.idm.srvc.policy.service.PolicyDataService", targetNamespace = "urn:idm.openiam.org/srvc/policy/service", portName = "PolicyWebServicePort", serviceName = "PolicyWebService")
@Service("policyDataService")
public class PolicyDataServiceImpl implements PolicyDataService {
    @Autowired
    private PolicyDefDAO policyDefDao;
    @Autowired
    private PolicyDAO policyDao;
    @Autowired
    private PolicyDefParamDAO policyDefParamDao;
    @Autowired
    private PolicyObjectAssocDAO objectAssoc;
    @Autowired
    private PolicyDozerConverter policyDozerConverter;
    @Autowired
    private PolicyDefDozerConverter policyDefDozerConverter;
    @Autowired
    private PolicyAttributeDozerConverter policyAttributeDozerConverter;
    @Autowired
    private PolicyDefParamDozerConverter policyDefParamDozerConverter;
    @Autowired
    private PolicyObjectAssocDozerConverter policyObjectAssocDozerConverter;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.policy.service.PolicyDataService#getPolicyTypes()
     */
    public List<String> getPolicyTypes() {
        final List<String> typeList = policyDefDao.findAllPolicyTypes();
        if (CollectionUtils.isEmpty(typeList))
            return null;
        return typeList;
    }

    public void addPolicyDefinition(PolicyDef val) {
        if (val == null) {
            throw new NullPointerException("PolicyDef is null");
        }
        policyDefDao.save(policyDefDozerConverter.convertToEntity(val, true));

    }

    public PolicyDef getPolicyDefinition(String policyDefId) {
        if (policyDefId == null) {
            throw new NullPointerException("policyDefId is null");
        }
        return policyDefDozerConverter.convertToDTO(
                policyDefDao.findById(policyDefId), false);

    }

    public void removePolicyDefinition(String definitionId) {
        if (definitionId == null) {
            throw new NullPointerException("definitionId is null");
        }
        final PolicyDef def = new PolicyDef(definitionId);
        policyDefDao
                .delete(policyDefDozerConverter.convertToEntity(def, false));

    }

    public void updatePolicyDefinition(PolicyDef val) {
        if (val == null) {
            throw new NullPointerException("PolicyDef is null");
        }
        policyDefDao
                .update(policyDefDozerConverter.convertToEntity(val, false));

    }

    /**
     * Returns an array of all policy definitions
     * @return
     */
    public List<PolicyDef> getAllPolicyDef() {
        final List<PolicyDef> defList = policyDefDozerConverter
                .convertToDTOList(policyDefDao.findAllPolicyDef(), false);
        if (CollectionUtils.isEmpty(defList))
            return null;
        return defList;
    }

    public PolicyDefDAO getPolicyDefDao() {
        return policyDefDao;
    }

    public void setPolicyDefDao(PolicyDefDAO policyDefDao) {
        this.policyDefDao = policyDefDao;
    }

    public PolicyDAO getPolicyDao() {
        return policyDao;
    }

    public void setPolicyDao(PolicyDAO policyDao) {
        this.policyDao = policyDao;
    }

    public void addPolicy(Policy val) {
        if (val == null) {
            throw new NullPointerException("Policy is null");
        }
        policyDao.save(policyDozerConverter.convertToEntity(val, false));

    }

    public List<Policy> getAllPolicies(String policyDefId) {
        if (policyDefId == null) {
            throw new NullPointerException("policyDefId is null");
        }
        final List<Policy> policyList = policyDozerConverter.convertToDTOList(
                policyDao.findAllPolicies(policyDefId), true);

        if (CollectionUtils.isEmpty(policyList)) {
            return new ArrayList<Policy>();
        }
        return policyList;

    }

    public Policy getPolicy(String policyId) {
        if (policyId == null) {
            throw new NullPointerException("PolicyId is null");
        }
        return policyDozerConverter.convertToDTO(policyDao.findById(policyId),
                false);
    }

    /**
     * Policy definitions parameters can be further categorized by parameter groups.
     * @param paramGroup
     * @return
     */
    public List<PolicyDefParam> getPolicyDefParamByGroup(String defId,
            String paramGroup) {
        if (paramGroup == null) {
            throw new NullPointerException("paramGroup is null");
        }
        return policyDefParamDozerConverter.convertToDTOList(
                policyDefParamDao.findPolicyDefParamByGroup(defId, paramGroup),
                true);
    }

    public void removePolicy(String policyId) {
        if (policyId == null) {
            throw new NullPointerException("PolicyId is null");
        }
        final Policy plcy = new Policy(policyId);
        policyDao.delete(policyDozerConverter.convertToEntity(plcy, false));

    }

    public void updatePolicy(Policy val) {
        if (val == null) {
            throw new NullPointerException("Policy is null");
        }
        policyDao.update(policyDozerConverter.convertToEntity(val, false));

    }

    public boolean isPolicyExist(String policyType, String policyName) {
        if (policyType == null) {
            throw new NullPointerException("policyType is null");
        }
        if (policyName == null) {
            throw new NullPointerException("policyName is null");
        }
        final List<Policy> policyList = policyDozerConverter.convertToDTOList(
                policyDao.findPolicyByName(policyType, policyName), true);
        return CollectionUtils.isNotEmpty(policyList);
    }

    public PolicyDefParamDAO getPolicyDefParamDao() {
        return policyDefParamDao;
    }

    public void setPolicyDefParamDao(PolicyDefParamDAO policyDefParamDao) {
        this.policyDefParamDao = policyDefParamDao;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.policy.service.PolicyDataService#associatePolicyToObject
     * (org.openiam.idm.srvc.policy.dto.PolicyObjectAssoc)
     */
    public void associatePolicyToObject(PolicyObjectAssoc assoc) {
        objectAssoc.save(policyObjectAssocDozerConverter.convertToEntity(assoc,
                true));

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openiam.idm.srvc.policy.service.PolicyDataService#
     * getAssociationsForPolicy(java.lang.String)
     */
    public List<PolicyObjectAssoc> getAssociationsForPolicy(String policyId) {
        return policyObjectAssocDozerConverter.convertToDTOList(
                objectAssoc.findByPolicy(policyId), true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.policy.service.PolicyDataService#updatePolicyAssociation
     * (org.openiam.idm.srvc.policy.dto.PolicyObjectAssoc)
     */
    public void updatePolicyAssociation(PolicyObjectAssoc assoc) {
        objectAssoc.update(policyObjectAssocDozerConverter.convertToEntity(
                assoc, true));

    }

    public PolicyObjectAssocDAO getObjectAssoc() {
        return objectAssoc;
    }

    public void setObjectAssoc(PolicyObjectAssocDAO objectAssoc) {
        this.objectAssoc = objectAssoc;
    }
}
