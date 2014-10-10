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
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.srvc.policy.domain.*;
import org.openiam.idm.srvc.policy.dto.ITPolicy;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.policy.dto.PolicyDefParam;

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
public class PolicyDataServiceImpl implements PolicyDataService {

	private static final Log log = LogFactory.getLog(PolicyDataServiceImpl.class);

    @Autowired
    private ITPolicyDAO itPolicyDao;

	/** The policy dozer converter. */
	@Autowired
    private PolicyDozerConverter policyDozerConverter;

    @Autowired
    private ITPolicyDozerConverter itPolicyDozerConverter;

	/** The policy def param dozer converter. */
	@Autowired
	private PolicyDefParamDozerConverter policyDefParamDozerConverter;
	
	@Autowired
	private PolicyService policyService;

	@Override
	@Transactional(readOnly=true)
	public Policy getPolicy(String policyId) {
		if (policyId == null) {
			throw new NullPointerException("PolicyId is null");
		}
		final PolicyEntity p = policyService.getPolicy(policyId);
		return policyDozerConverter.convertToDTO(p, true);
	}

	@Override
	@Deprecated
	@Transactional(readOnly=true)
	public List<Policy> getAllPolicies(String policyDefId, final int from, final int size) {
		final PolicySearchBean searchBean = new PolicySearchBean();
		searchBean.setPolicyDefId(policyDefId);
		searchBean.setDeepCopy(true);
		return findBeans(searchBean, from, size);
	}

	@Override
	@Transactional(readOnly=true)
	public List<PolicyDefParam> getAllPolicyAttributes(String policyDefId,
			String pswdGroup) {
		if (policyDefId == null) {
			throw new NullPointerException("policyDefId is null");
		}

		final List<PolicyDefParam> policyList = policyDefParamDozerConverter
				.convertToDTOList(policyService.findPolicyDefParamByGroup(
						policyDefId, pswdGroup), true);

		if (CollectionUtils.isEmpty(policyList)) {
			return null;
		}
		return policyList;

	}

	@Override
	public Response savePolicy(final Policy policy) {
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

			final List<PolicyEntity> found = policyService.findPolicyByName(policy.getPolicyDefId(), policy.getName());
			if (found != null && found.size() > 0) {
				if (StringUtils.isBlank(policy.getId()) && found != null) {
					throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
				}

				if (StringUtils.isNotBlank(policy.getId())
						&& !policy.getId().equals(
								found.get(0).getId())) {
					throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
				}
			}
			final PolicyEntity pe = policyDozerConverter.convertToEntity(policy, true);

			policyService.save(pe);
			response.setResponseValue(pe.getId());
		} catch (BasicDataServiceException e) {
			log.warn("Can't save policty", e);
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
		} catch (Throwable e) {
			log.error("Can't perform operation", e);
			response.setErrorText(e.getMessage());
			response.setStatus(ResponseStatus.FAILURE);
		}

		return response;
	}
	
	@Override
	@Deprecated
	public Response addPolicy(Policy policy) {
		return savePolicy(policy);
	}

	@Override
	@Deprecated
	public Response updatePolicy(Policy policy) {
		return savePolicy(policy);
	}

	
	@Override
	public Response deletePolicy(String policyId) {

		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (policyId == null) {
				throw new BasicDataServiceException(
						ResponseCode.INVALID_ARGUMENTS);
			}
			
			

			policyService.delete(policyId);
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
	@Override
	@Transactional(readOnly=true)
	public List<Policy> findBeans(final PolicySearchBean searchBean, int from, int size) {
        return policyDozerConverter.convertToDTOList(policyService.findBeans(searchBean, from, size), true);
	}

	@Override
	public int count(PolicySearchBean searchBean) {
		return policyService.count(searchBean);
	}

    @Override
    @Transactional(readOnly=true)
    public ITPolicy findITPolicy() {
        return itPolicyDozerConverter.convertToDTO(itPolicyDao.findITPolicy(), true);
    }

    @Override
    @Transactional(readOnly=true)
    public Response resetITPolicy() {
        final Response response = new Response(ResponseStatus.SUCCESS);
        final ITPolicyEntity itPolicy = itPolicyDao.findITPolicy();
        if (itPolicy != null) {
            itPolicyDao.delete(itPolicy);
        }
        return response;
    }

    @Override
    @Transactional
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
