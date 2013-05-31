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
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.jws.WebService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.dozer.converter.PolicyAttributeDozerConverter;
import org.openiam.dozer.converter.PolicyDefDozerConverter;
import org.openiam.dozer.converter.PolicyDefParamDozerConverter;
import org.openiam.dozer.converter.PolicyDozerConverter;
import org.openiam.dozer.converter.PolicyObjectAssocDozerConverter;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.org.service.OrganizationDataServiceImpl;
import org.openiam.idm.srvc.policy.domain.PolicyAttributeEntity;
import org.openiam.idm.srvc.policy.domain.PolicyDefEntity;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.domain.PolicyObjectAssocEntity;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.policy.dto.PolicyDef;
import org.openiam.idm.srvc.policy.dto.PolicyDefParam;
import org.openiam.idm.srvc.policy.dto.PolicyObjectAssoc;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.user.domain.UserEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// TODO: Auto-generated Javadoc
/**
 * PolicyDataService is used create and manage policies. Enforcement of these
 * policies is handled through policy specific services and policy enforcement
 * points.
 * 
 * @author suneet
 * 
 */

@WebService(endpointInterface = "org.openiam.idm.srvc.policy.service.PolicyDataService", targetNamespace = "urn:idm.openiam.org/srvc/policy/service", portName = "PolicyWebServicePort", serviceName = "PolicyWebService")
@Service("policyDataService")
@Transactional
public class PolicyDataServiceImpl implements PolicyDataService {
	
	private static final Log log = LogFactory.getLog(PolicyDataServiceImpl.class);
	
	/** The policy dao. */
	@Autowired
	private PolicyDAO policyDao;
	
	/** The policy def param dao. */
	@Autowired
	private PolicyDefParamDAO policyDefParamDao;
	
	/** The policy dozer converter. */
	@Autowired
	private PolicyDozerConverter policyDozerConverter;
	
	/** The policy def param dozer converter. */
	@Autowired
	private PolicyDefParamDozerConverter policyDefParamDozerConverter;
	
	/** The policy attribute dao. */
	@Autowired
	private PolicyAttributeDAO policyAttributeDao;
	
	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.policy.service.PolicyDataService#getPolicy(java.lang.String)
	 */
	public Policy getPolicy(String policyId) {
		if (policyId == null) {
			throw new NullPointerException("PolicyId is null");
		}
		PolicyEntity p = policyDao.findById(policyId);
		if (p == null)
			return null;
		return policyDozerConverter.convertToDTO(p, true);
	}

	/**
	 * Gets the policy def param dao.
	 *
	 * @return the policy def param dao
	 */
	public PolicyDefParamDAO getPolicyDefParamDao() {
		return policyDefParamDao;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.policy.service.PolicyDataService#getAllPolicies(java.lang.String)
	 */
	public List<Policy> getAllPolicies(String policyDefId) {
		if (policyDefId == null) {
			throw new NullPointerException("policyDefId is null");
		}
		final List<Policy> policyList = policyDozerConverter.convertToDTOList(
				policyDao.findAllPolicies(policyDefId), true);

		if (CollectionUtils.isEmpty(policyList)) {
			return null;
		}
		return policyList;

	}
	
	
	
	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.policy.service.PolicyDataService#getAllPolicyAttributes(java.lang.String)
	 */
	public List<PolicyDefParam> getAllPolicyAttributes(String policyDefId, String pswdGroup){
		if (policyDefId == null) {
			throw new NullPointerException("policyDefId is null");
		}

		
		final List<PolicyDefParam> policyList =  policyDefParamDozerConverter.convertToDTOList(
				policyDefParamDao.findPolicyDefParamByGroup(policyDefId, pswdGroup), true);
		
		
		if (CollectionUtils.isEmpty(policyList)) {
			return null;
		}
		return policyList;

	}
	
	
	
	
	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.policy.service.PolicyDataService#addPolicy(org.openiam.idm.srvc.policy.dto.Policy)
	 */
	@Override
	public Response addPolicy(Policy policy) {
	
		
		return saveOrUpdatePolicy(policy);
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.policy.service.PolicyDataService#updatePolicy(org.openiam.idm.srvc.policy.dto.Policy)
	 */
	@Override
	public Response updatePolicy(Policy policy) {
		
		return saveOrUpdatePolicy(policy);
	}
	
	
	
	/**
	 * Save or update policy.
	 *
	 * @param policy the policy
	 * @return the response
	 */
	private Response saveOrUpdatePolicy( Policy policy) {
		 final Response response = new Response(ResponseStatus.SUCCESS);
		
		
		 try {
	            if (policy == null) {
	                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
	            }
	            
	            PolicyEntity pe=policyDozerConverter.convertToEntity(policy, true);
	            
	            /* merge */
				if (StringUtils.isNotBlank(pe.getPolicyId())) {
					final PolicyEntity poObject = policyDao.findById(policy.getPolicyId());
				
					// TODO: extend this merge
					poObject.setCreateDate(pe.getCreateDate());
					poObject.setCreatedBy(pe.getCreatedBy());
					poObject.setDescription(pe.getDescription());
					poObject.setPolicyId(pe.getPolicyId());
					poObject.setPolicyDefId(pe.getPolicyDefId());
					poObject.setName(pe.getName());
					poObject.setLastUpdate(pe.getLastUpdate());
					poObject.setLastUpdatedBy(pe.getLastUpdatedBy());
					poObject.setRule(pe.getRule());
					poObject.setRuleSrcUrl(pe.getRuleSrcUrl());
					poObject.setStatus(pe.getStatus());
					
				  //Updating Policy.
					policyDao.update(poObject);
					
					//updating policy attribute.
					Set<PolicyAttributeEntity>   policyAttributes= pe.getPolicyAttributes();
					for(PolicyAttributeEntity policyAttribute: policyAttributes){
						policyAttributeDao.update(policyAttribute);
					}
				
					
				} else {
					//creating new Policy
					pe =policyDao.add(pe);
					response.setResponseValue(pe.getPolicyId());
					//creating new Policy Attribute.
					/*Set<PolicyAttributeEntity>   policyAttributes= pe.getPolicyAttributes();
					for(PolicyAttributeEntity policyAttribute: policyAttributes){
						policyAttribute.setPolicyId(pe.getPolicyId());
						policyAttributeDao.add(policyAttribute);
					}*/
					
				}
	        } catch (BasicDataServiceException e) {
	        
	            response.setErrorCode(e.getCode());
	            response.setStatus(ResponseStatus.FAILURE);
	        } catch (Throwable e) {
	            log.error("Can't perform operation", e);
	            response.setErrorText(e.getMessage());
	            response.setStatus(ResponseStatus.FAILURE);
	        }

			return response;
		
		}


	
	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.policy.service.PolicyDataService#deletePolicy(java.lang.String)
	 */
	@Override
	public Response deletePolicy(String policyId) {
		
		final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (policyId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            PolicyEntity pe=policyDao.findById(policyId);
            policyDao.delete(pe);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
          log.error("Can't save policy type", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }
		

	
}
