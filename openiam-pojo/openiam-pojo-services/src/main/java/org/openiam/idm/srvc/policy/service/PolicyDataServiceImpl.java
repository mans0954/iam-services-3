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
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.srvc.policy.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

// TODO: Auto-generated Javadoc

/**
 * PolicyDataService is used create and manage policies. Enforcement of these
 * policies is handled through policy specific services and policy enforcement
 * points.
 *
 * @author suneet
 */

@WebService(endpointInterface = "org.openiam.idm.srvc.policy.service.PolicyDataService", targetNamespace = "urn:idm.openiam.org/srvc/policy/service", portName = "PolicyWebServicePort", serviceName = "PolicyWebService")
@Service("policyDataService")
public class PolicyDataServiceImpl implements PolicyDataService {

    private static final Log log = LogFactory.getLog(PolicyDataServiceImpl.class);

    @Autowired
    private PolicyService policyService;

    @Override
    @Cacheable(value = "policies", key = "{#policyId}")
    public Policy getPolicy(String policyId) {
        if (policyId == null) {
            throw new NullPointerException("PolicyId is null");
        }
        return policyService.getPolicy(policyId);
    }

    @Override
    @Deprecated
    public List<Policy> getAllPolicies(String policyDefId, final int from, final int size) {
        final PolicySearchBean searchBean = new PolicySearchBean();
        searchBean.setPolicyDefId(policyDefId);
        searchBean.setDeepCopy(true);
        return findBeans(searchBean, from, size);
    }

    @Override
    public List<PolicyDefParam> getAllPolicyAttributes(String policyDefId,
                                                       String pswdGroup) {
        if (policyDefId == null) {
            throw new NullPointerException("policyDefId is null");
        }

        final List<PolicyDefParam> policyList = policyService.findPolicyDefParamByGroup(
                policyDefId, pswdGroup);


        if (CollectionUtils.isEmpty(policyList)) {
            return null;
        }
        return policyList;

    }

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

            final List<Policy> found = policyService.findPolicyByName(policy.getPolicyDefId(), policy.getName());
            if (found != null && found.size() > 0) {
                if (StringUtils.isBlank(policy.getPolicyId())) {
                    throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
                }

                if (StringUtils.isNotBlank(policy.getPolicyId())
                        && !policy.getPolicyId().equals(
                        found.get(0).getPolicyId())) {
                    throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
                }
            }

            if (CollectionUtils.isNotEmpty(policy.getPolicyAttributes())) {
                for (PolicyAttribute pa : policy.getPolicyAttributes()) {
                    boolean isPasswordPolicy = PolicyConstants.PSWD_COMPOSITION.equals(pa.getOperation()) ||
                            PolicyConstants.PSWD_CHANGE_RULE.equals(pa.getOperation()) || PolicyConstants.FORGET_PSWD.equals(pa.getOperation());
                    String op = pa.getOperation();
                    if ((isPasswordPolicy && StringUtils.isBlank(op)) || StringUtils.isBlank(pa.getName())) {
                        throw new BasicDataServiceException(ResponseCode.INVALID_VALUE);
                    }
                    if (StringUtils.isNotBlank(op)) {
                        switch (op) {
                            case PolicyConstants.SELECT:
                            case PolicyConstants.STRING:
                                if (pa.isRequired() && StringUtils.isBlank(pa.getValue1())) {
                                    throw new BasicDataServiceException(ResponseCode.POLICY_ATTRIBUTES_EMPTY_VALUE);
                                }
                                break;
                            case PolicyConstants.RANGE:
                                if (isPasswordPolicy && pa.isRequired() && StringUtils.isBlank(pa.getValue1()) && StringUtils.isBlank(pa.getValue2())) {
                                    throw new BasicDataServiceException(ResponseCode.POLICY_ATTRIBUTES_EMPTY_VALUE);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            String id = policyService.save(policy);

            response.setResponseValue(id);
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
    public List<PolicyObjectAssoc> getAssociationsForPolicy(String policyId) {
        return policyService.getAssociationsForPolicy(policyId);
    }

    @Override
    public Response savePolicyAssoc(PolicyObjectAssoc poa) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (poa == null || poa.getPolicyId() == null) {
                throw new BasicDataServiceException(
                        ResponseCode.INVALID_ARGUMENTS);
            }

           String id = policyService.savePolicyAssoc(poa);
           response.setResponseValue(id);
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
        return policyService.findBeans(searchBean, from, size);
    }

    @Override
    public int count(PolicySearchBean searchBean) {
        return policyService.count(searchBean);
    }

    @Override
    public ITPolicy findITPolicy() {
        return policyService.findITPolicy();
    }

    @Override
    public Response resetITPolicy() {
       final Response response = new Response(ResponseStatus.SUCCESS);
       policyService.resetITPolicy();
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
            String id = policyService.saveITPolicy(itPolicy);
            response.setResponseValue(id);

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
