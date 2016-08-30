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
package org.openiam.srvc.common;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.request.*;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.policy.dto.ITPolicy;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.policy.dto.PolicyDefParam;
import org.openiam.idm.srvc.policy.dto.*;

import org.openiam.idm.srvc.policy.service.*;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.constants.PolicyAPI;
import org.openiam.mq.constants.RoleAPI;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// TODO: Auto-generated Javadoc

/**
 * PolicyDataService is used create and manage policies. Enforcement of these
 * policies is handled through policy specific services and policy enforcement
 * points.
 *
 * @author suneet
 */

@WebService(endpointInterface = "org.openiam.srvc.common.PolicyDataService", targetNamespace = "urn:idm.openiam.org/srvc/policy/service", portName = "PolicyWebServicePort", serviceName = "PolicyWebService")
@Service("policyDataService")
public class PolicyDataServiceImpl extends AbstractApiService implements PolicyDataService {

    private static final Log log = LogFactory.getLog(PolicyDataServiceImpl.class);

    @Autowired
    private PolicyService policyService;

    protected PolicyDataServiceImpl() {
        super(OpenIAMQueue.PolicyQueue);
    }

    @Override
    public Policy getPolicy(String policyId) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(policyId);
        return this.manageApiRequest(PolicyAPI.FindBeans, request,
                PolicyGetResponse.class).getPolicy();
    }

    @Override
    public List<PolicyDefParam> getAllPolicyAttributes(String policyDefId,
                                                       String pswdGroup) {
        PolicyGetAppPolicyAttrubutesRequest request = new PolicyGetAppPolicyAttrubutesRequest();
        request.setId(policyDefId);
        request.setPswdGroup(pswdGroup);
        return this.manageApiRequest(PolicyAPI.GetAllPolicyAttributes, request,
                PolicyDefParamFindBeansResponse.class).getPolicyDefParams();

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

            final PolicySearchBean sb = new PolicySearchBean();
            sb.setName(policy.getName());
            sb.setPolicyDefId(policy.getPolicyDefId());

            final List<Policy> found = policyService.findBeans(sb, 0, Integer.MAX_VALUE);
            if (found != null && found.size() > 0) {
                if (StringUtils.isBlank(policy.getId())) {
                    throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
                }

                if (StringUtils.isNotBlank(policy.getId())
                        && !policy.getId().equals(
                        found.get(0).getId())) {
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
            policyService.save(policy);

            response.setResponseValue(policy.getId());
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
    public List<Policy> findBeans(final PolicySearchBean searchBean, int from, int size) {
        return this.manageApiRequest(PolicyAPI.FindBeans, new BaseSearchServiceRequest<>(searchBean, from, size),
                PolicyFindBeansResponse.class).getPolicies();
    }

    @Override
    public int count(PolicySearchBean searchBean) {
        return this.manageApiRequest(PolicyAPI.Count, new BaseSearchServiceRequest<>(searchBean, -1, -1),
                CountResponse.class).getRowCount();
    }

    @Override
    public ITPolicy findITPolicy() {
        return this.manageApiRequest(PolicyAPI.FindITPolicy, new BaseServiceRequest(),
                ITPolicyResponse.class).getItPolicy();
    }

    @Override
    public Response resetITPolicy() {
        return this.manageApiRequest(PolicyAPI.ResetITPolicy, new BaseServiceRequest(),
                Response.class);
    }

    @Override
    public Response saveOrUpdateITPolicy(ITPolicy itPolicy) {
        PolicySaveOrUpdateITPolicyRequest request = new PolicySaveOrUpdateITPolicyRequest();
        request.setItPolicy(itPolicy);
        return this.manageApiRequest(PolicyAPI.SaveOrUpdateITPolicy, request,
                BooleanResponse.class).convertToBase();
    }

}
