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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.jws.WebService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.dozer.converter.ITPolicyDozerConverter;
import org.openiam.dozer.converter.PolicyDefParamDozerConverter;
import org.openiam.dozer.converter.PolicyDozerConverter;
import org.openiam.dozer.converter.PolicyObjectAssocDozerConverter;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.srvc.policy.domain.*;
import org.openiam.idm.srvc.policy.dto.ITPolicy;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyDefParam;
import org.openiam.idm.srvc.policy.dto.PolicyObjectAssoc;

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

	private static final Log log = LogFactory
			.getLog(PolicyDataServiceImpl.class);

	/** The policy dao. */
	@Autowired
	private PolicyDAO policyDao;

    @Autowired
    private ITPolicyDAO itPolicyDao;

	/** The policy def param dao. */
	@Autowired
	private PolicyDefParamDAO policyDefParamDao;

	@Autowired
	private PolicyObjectAssocDAO policyObjectAssocDAO;

	/** The policy dozer converter. */
	@Autowired
    private PolicyDozerConverter policyDozerConverter;

    @Autowired
    private ITPolicyDozerConverter itPolicyDozerConverter;

	@Autowired
	private PolicyObjectAssocDozerConverter policyAssocObjectDozerConverter;

	/** The policy def param dozer converter. */
	@Autowired
	private PolicyDefParamDozerConverter policyDefParamDozerConverter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openiam.idm.srvc.policy.service.PolicyDataService#getPolicy(java.
	 * lang.String)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openiam.idm.srvc.policy.service.PolicyDataService#getAllPolicies(
	 * java.lang.String)
	 */
	public List<Policy> getAllPolicies(String policyDefId, final int from, final int size) {
		if (policyDefId == null) {
			throw new NullPointerException("policyDefId is null");
		}
		final List<Policy> policyList = policyDozerConverter.convertToDTOList(
				policyDao.findAllPolicies(policyDefId, from , size), true);

		if (CollectionUtils.isEmpty(policyList)) {
			return null;
		}
		return policyList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openiam.idm.srvc.policy.service.PolicyDataService#getAllPolicyAttributes
	 * (java.lang.String)
	 */
	public List<PolicyDefParam> getAllPolicyAttributes(String policyDefId,
			String pswdGroup) {
		if (policyDefId == null) {
			throw new NullPointerException("policyDefId is null");
		}

		final List<PolicyDefParam> policyList = policyDefParamDozerConverter
				.convertToDTOList(policyDefParamDao.findPolicyDefParamByGroup(
						policyDefId, pswdGroup), true);

		if (CollectionUtils.isEmpty(policyList)) {
			return null;
		}
		return policyList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openiam.idm.srvc.policy.service.PolicyDataService#addPolicy(org.openiam
	 * .idm.srvc.policy.dto.Policy)
	 */
	@Override
	public Response addPolicy(Policy policy) {

		return saveOrUpdatePolicy(policy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openiam.idm.srvc.policy.service.PolicyDataService#updatePolicy(org
	 * .openiam.idm.srvc.policy.dto.Policy)
	 */
	@Override
	public Response updatePolicy(Policy policy) {

		return saveOrUpdatePolicy(policy);
	}

	/**
	 * Save or update policy.
	 * 
	 * @param policy
	 *            the policy
	 * @return the response
	 */
	private Response saveOrUpdatePolicy(Policy policy) {
		final Response response = new Response(ResponseStatus.SUCCESS);

		try {
			if (policy == null) {
				throw new BasicDataServiceException(
						ResponseCode.INVALID_ARGUMENTS);
			}
			if (StringUtils.isBlank(policy.getName())) {
				throw new BasicDataServiceException(
						ResponseCode.POLICY_NAME_NOT_SET);
			}

			final List<PolicyEntity> found = policyDao.findPolicyByName(
					policy.getPolicyDefId(), policy.getName());
			if (found != null && found.size() > 0) {
				if (StringUtils.isBlank(policy.getPolicyId()) && found != null) {
					throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
				}

				if (StringUtils.isNotBlank(policy.getPolicyId())
						&& !policy.getPolicyId().equals(
								found.get(0).getPolicyId())) {
					throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
				}
			}
			PolicyEntity pe = policyDozerConverter
					.convertToEntity(policy, true);

			/* merge */
			if (StringUtils.isNotBlank(pe.getPolicyId())) {
				final PolicyEntity poObject = policyDao.findById(policy
						.getPolicyId());

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
				poObject.setPolicyAttributes(pe.getPolicyAttributes());
				// Updating Policy.
				policyDao.update(poObject);

				response.setResponseValue(pe.getPolicyId());

			} else {
				// creating new Policy
				pe = policyDao.add(pe);
				response.setResponseValue(pe.getPolicyId());

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openiam.idm.srvc.policy.service.PolicyDataService#deletePolicy(java
	 * .lang.String)
	 */
	@Override
	public Response deletePolicy(String policyId) {

		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (policyId == null) {
				throw new BasicDataServiceException(
						ResponseCode.INVALID_ARGUMENTS);
			}

			PolicyEntity pe = policyDao.findById(policyId);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openiam.idm.srvc.policy.service.PolicyDataService#
	 * getAssociationsForPolicy(java.lang.String)
	 */
	@Override
	public List<PolicyObjectAssoc> getAssociationsForPolicy(String policyId) {

		List<PolicyObjectAssoc> policyObjectAssoc = policyAssocObjectDozerConverter
				.convertToDTOList(policyObjectAssocDAO.findByPolicy(policyId),
						true);

		return policyObjectAssoc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openiam.idm.srvc.policy.service.PolicyDataService#savePolicyAssoc
	 * (org.openiam.idm.srvc.policy.dto.PolicyObjectAssoc)
	 */
	@Override
	public Response savePolicyAssoc(PolicyObjectAssoc poa) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (poa.getPolicyId() == null) {
				throw new BasicDataServiceException(
						ResponseCode.INVALID_ARGUMENTS);
			}
			PolicyObjectAssocEntity poaEntity = policyAssocObjectDozerConverter
					.convertToEntity(poa, true);
			if (poaEntity.getObjectId() == null
					&& poaEntity.getObjectId().isEmpty()) {
				poaEntity.setObjectId(null);
				poaEntity = policyObjectAssocDAO.add(poaEntity);
				response.setResponseValue(poaEntity.getPolicyObjectId());
			} else {
				policyObjectAssocDAO.update(poaEntity);
			}
		} catch (BasicDataServiceException e) {

			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(e.getCode());
		} catch (Throwable e) {
			log.error("Can't save associate policy type", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorText(e.getMessage());
		}
		return response;
	}

	@Override
	public List<Policy> findBeans(final PolicySearchBean searchBean, int from, int size) {
        return policyDozerConverter.convertToDTOList(policyDao.getByExample(searchBean, from, size), true);
	}

	@Override
	public int count(PolicySearchBean searchBean) {
		return policyDao.count(searchBean);
	}

    @Override
    public ITPolicy findITPolicy() {
        return itPolicyDozerConverter.convertToDTO(itPolicyDao.findITPolicy(), true);
    }

    @Override
    public Response resetITPolicy() {
        final Response response = new Response(ResponseStatus.SUCCESS);
        final ITPolicyEntity itPolicy = itPolicyDao.findITPolicy();
        if (itPolicy != null) {
            itPolicyDao.delete(itPolicy);
        }
        return response;
    }

    @Override
    public Response saveOrUpdateITPolicy(ITPolicy itPolicy) {
        final Response response = new Response(ResponseStatus.SUCCESS);

        try {
            if (itPolicy == null) {
                throw new BasicDataServiceException(
                        ResponseCode.INVALID_ARGUMENTS);
            }

            final ITPolicy found = findITPolicy();
            if (found != null && !found.getPolicyId().equals(itPolicy.getPolicyId())) {
                throw new BasicDataServiceException(ResponseCode.IT_POLICY_EXISTS);
            }
            if (found != null) {
                itPolicy.setCreateDate(found.getCreateDate());
                itPolicy.setCreatedBy(found.getCreatedBy());
            }
            ITPolicyEntity pe = itPolicyDao.merge(
                    itPolicyDozerConverter.convertToEntity(itPolicy, true));
            response.setResponseValue(pe.getPolicyId());

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

}
