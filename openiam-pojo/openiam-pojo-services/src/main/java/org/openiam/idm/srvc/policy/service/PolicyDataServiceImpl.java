/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the Lesser GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.idm.srvc.policy.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.dozer.DozerBeanMapper;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyDef;
import org.openiam.idm.srvc.policy.dto.PolicyDefParam;
import org.openiam.idm.srvc.policy.dto.PolicyObjectAssoc;
import org.openiam.util.DozerMappingType;
import org.springframework.beans.factory.annotation.Required;

/**
 * PolicyDataService is used create and manage policies. 
 * Enforcement of these policies is handled through policy specific services and policy enforcement points. 
 * @author suneet
 *
 */
@WebService(endpointInterface = "org.openiam.idm.srvc.policy.service.PolicyDataService", 
		targetNamespace = "urn:idm.openiam.org/srvc/policy/service", 
		portName = "PolicyWebServicePort",
		serviceName = "PolicyWebService")
public class PolicyDataServiceImpl implements PolicyDataService {

	private PolicyDefDAO policyDefDao;
	private PolicyDAO policyDao;
	private PolicyDefParamDAO policyDefParamDao;
	private PolicyObjectAssocDAO objectAssoc;
	private Map<DozerMappingType, DozerBeanMapper> dozerMap;
	
	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.policy.service.PolicyDataService#getPolicyTypes()
	 */
	public String[] getPolicyTypes() {
		// TODO Auto-generated method stub
		final List<String> typeList = policyDefDao.findAllPolicyTypes();
		if (CollectionUtils.isEmpty(typeList))
			return null;
		final int size = typeList.size();
		final String[] strAry = new String[size];
		typeList.toArray(strAry);
		return strAry;
	}

	public void addPolicyDefinition(PolicyDef val) {
		if (val == null) {
			throw new NullPointerException("PolicyDef is null");
		}
		policyDefDao.add(val);
		
	}

	public PolicyDef getPolicyDefinition(String policyDefId) {
		if (policyDefId == null) {
			throw new NullPointerException("policyDefId is null");
		}
		return getDozerMappedPolicyDef(policyDefDao.findById(policyDefId));

	}

	public void removePolicyDefinition(String definitionId) {
		if (definitionId == null) {
			throw new NullPointerException("definitionId is null");
		}
		final PolicyDef def = new PolicyDef(definitionId);
		policyDefDao.remove(def);
		
	}

	public void updatePolicyDefinition(PolicyDef val) {
		if (val == null) {
			throw new NullPointerException("PolicyDef is null");
		}
		policyDefDao.update(val);
		
	}
	/**
	 * Returns an array of all policy definitions
	 * @return
	 */
	public PolicyDef[] getAllPolicyDef() {
		final List<PolicyDef> defList =  getDozerMappedPolicyDefList(policyDefDao.findAllPolicyDef());
		if (CollectionUtils.isEmpty(defList)) {
			return null;
		}
		final int size = defList.size();
		final PolicyDef[] defAry = new PolicyDef[size];
		defList.toArray(defAry);
		return defAry;
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
		policyDao.add(val);
		
	}

	public List<Policy> getAllPolicies(String policyDefId) {
		if (policyDefId == null) {
			throw new NullPointerException("policyDefId is null");
		}
		final List<Policy> policyList = getDozerMappedPolicyList(policyDao.findAllPolicies(policyDefId));
		
		if (CollectionUtils.isEmpty(policyList)) {
			return null;
		}
		return policyList;
	
	}

	public Policy getPolicy(String policyId) {
		if (policyId == null) {
			throw new NullPointerException("PolicyId is null");
		}
		return getDozerMappedPolicy(policyDao.findById(policyId));
	}
	
	/**
	 * Policy definitions parameters can be further categorized by parameter groups.
	 * @param paramGroup
	 * @return
	 */
	public List<PolicyDefParam> getPolicyDefParamByGroup(String defId, String paramGroup) {
		if (paramGroup == null) {
			throw new NullPointerException("paramGroup is null");
		}
		return getDozerMappedPolicyDefParamList(policyDefParamDao.findPolicyDefParamByGroup(defId, paramGroup));
	}

	public void removePolicy(String policyId) {
		if (policyId == null) {
			throw new NullPointerException("PolicyId is null");
		}
		final Policy plcy = new Policy(policyId);
		policyDao.remove(plcy);
		
	}

	public void updatePolicy(Policy val) {
		if (val == null) {
			throw new NullPointerException("Policy is null");
		}
		policyDao.update(val);
		
	}

	public boolean isPolicyExist(String policyType, String policyName) {
		if (policyType == null) {
			throw new NullPointerException("policyType is null");
		}
		if (policyName == null) {
			throw new NullPointerException("policyName is null");
		}
		final List<Policy> policyList = policyDao.findPolicyByName(policyType, policyName);
		return CollectionUtils.isNotEmpty(policyList);
	}

	public PolicyDefParamDAO getPolicyDefParamDao() {
		return policyDefParamDao;
	}

	public void setPolicyDefParamDao(PolicyDefParamDAO policyDefParamDao) {
		this.policyDefParamDao = policyDefParamDao;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.policy.service.PolicyDataService#associatePolicyToObject(org.openiam.idm.srvc.policy.dto.PolicyObjectAssoc)
	 */
	public void associatePolicyToObject(PolicyObjectAssoc assoc) {
		objectAssoc.add(assoc);
		
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.policy.service.PolicyDataService#getAssociationsForPolicy(java.lang.String)
	 */
	public List<PolicyObjectAssoc> getAssociationsForPolicy(String policyId) {
		return objectAssoc.findByPolicy(policyId);
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.policy.service.PolicyDataService#updatePolicyAssociation(org.openiam.idm.srvc.policy.dto.PolicyObjectAssoc)
	 */
	public void updatePolicyAssociation(PolicyObjectAssoc assoc) {
		objectAssoc.update(assoc);
		
	}

	public PolicyObjectAssocDAO getObjectAssoc() {
		return objectAssoc;
	}

	public void setObjectAssoc(PolicyObjectAssocDAO objectAssoc) {
		this.objectAssoc = objectAssoc;
	}


	@Required
	public void setDozerMap(final Map<DozerMappingType, DozerBeanMapper> dozerMap) {
		this.dozerMap = dozerMap;
	}
	
	private List<PolicyDef> getDozerMappedPolicyDefList(final List<PolicyDef> policyDefList) {
		final List<PolicyDef> convertedPolicyDefList = new LinkedList<PolicyDef>();
		if(CollectionUtils.isNotEmpty(policyDefList)) {
			for(final PolicyDef policyDef : policyDefList) {
				convertedPolicyDefList.add(dozerMap.get(DozerMappingType.DEEP).map(policyDef, PolicyDef.class));
			}
		}
		return convertedPolicyDefList;
	}
	
	private List<Policy> getDozerMappedPolicyList(final List<Policy> policyList) {
		final List<Policy> convertedPolicyList = new LinkedList<Policy>();
		if(CollectionUtils.isNotEmpty(policyList)) {
			for(final Policy policy : policyList) {
				convertedPolicyList.add(dozerMap.get(DozerMappingType.DEEP).map(policy, Policy.class));
			}
		}
		return convertedPolicyList;
	}
	
	private Policy getDozerMappedPolicy(final Policy policy) {
		Policy retVal = null;
		if(policy != null) {
			retVal = dozerMap.get(DozerMappingType.DEEP).map(policy, Policy.class);
		}
		return retVal;
	}
	
	private List<PolicyDefParam> getDozerMappedPolicyDefParamList(final List<PolicyDefParam> policyDefParamList) {
		final List<PolicyDefParam> convertedList = new LinkedList<PolicyDefParam>();
		if(CollectionUtils.isNotEmpty(policyDefParamList)) {
			for(final PolicyDefParam param : policyDefParamList) {
				convertedList.add(dozerMap.get(DozerMappingType.DEEP).map(param, PolicyDefParam.class));
			}
		}
		return convertedList;
	}
	
	private PolicyDef getDozerMappedPolicyDef(final PolicyDef policyDef) {
		PolicyDef retVal = null;
		if(policyDef != null) {
			retVal = dozerMap.get(DozerMappingType.DEEP).map(policyDef, PolicyDef.class);
		}
		return retVal;
	}
}
