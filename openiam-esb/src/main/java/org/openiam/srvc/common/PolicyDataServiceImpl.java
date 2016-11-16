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

import org.openiam.base.request.*;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.srvc.policy.dto.ITPolicy;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyDefParam;

import org.openiam.idm.srvc.policy.service.*;
import org.openiam.mq.constants.queue.OpenIAMQueue;
import org.openiam.mq.constants.PolicyAPI;
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
        return this.manageApiRequest(PolicyAPI.GetPolicy, request,
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
        PolicySavePolicyRequest policySavePolicyRequest = new PolicySavePolicyRequest();
        policySavePolicyRequest.setPolicy(policy);
        return this.manageApiRequest(PolicyAPI.SavePolicy, policySavePolicyRequest,
                StringResponse.class).convertToBase();
    }

    @Override
    public Response deletePolicy(String policyId) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(policyId);
        return this.manageApiRequest(PolicyAPI.DeletePolicy, request,
                BooleanResponse.class).convertToBase();
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
        return this.manageApiRequest(PolicyAPI.FindITPolicy, new EmptyServiceRequest(),
                ITPolicyResponse.class).getItPolicy();
    }

    @Override
    public Response resetITPolicy() {
        return this.manageApiRequest(PolicyAPI.ResetITPolicy, new EmptyServiceRequest(),
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
