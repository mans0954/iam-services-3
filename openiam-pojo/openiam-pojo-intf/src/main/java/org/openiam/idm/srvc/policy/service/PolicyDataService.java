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
 * PolicyDataService is used create and manage policies. Enforcement of these
 * policies is handled through policy specific services and policy enforcement
 * points.
 */
package org.openiam.idm.srvc.policy.service;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.domain.PolicyObjectAssocEntity;
import org.openiam.idm.srvc.policy.dto.*;
import org.openiam.idm.srvc.res.dto.Resource;


/**
 * @author suneet
 */
@WebService
public interface PolicyDataService {

	public List<Policy> findBeans(final PolicySearchBean searchBean, final int from, final int size);
	
	public int count(final PolicySearchBean searchBean);
	
	/**
	 * @param policyDefId
	 * @return
	 */
	List<Policy> getAllPolicies(String policyDefId, final int from, final int size);

	/**
	 * @param policyId
	 * @return
	 */
	Policy getPolicy(String policyId);
	
   
   /**
    * Adds the policy.
    *
    * @param policy the policy
    * @return the response
    */
   Response addPolicy(Policy policy);
	
	
	/**
	 * Update policy.
	 *
	 * @param policy the policy
	 * @return the response
	 */
	Response updatePolicy(Policy  policy);
	
	
	/**
	 * Delete policy.
	 *
	 * @param policyId the policy id
	 * @return the response
	 */
	Response deletePolicy(String policyId);
	
	
	/**
	 * Gets the all policy attributes.
	 *
	 * @param policyDefId the policy def id
	 * @param pswdGroup the pswd group
	 * @return the all policy attributes
	 */
	List<PolicyDefParam> getAllPolicyAttributes(String policyDefId, String pswdGroup);
	
	
	/**
	 * Gets the associations for policy.
	 *
	 * @param policyId the policy id
	 * @return the associations for policy
	 */
	List<PolicyObjectAssoc> getAssociationsForPolicy(String policyId);
	
	/**
	 * Save policy assoc.
	 *
	 * @param poa the PolicyObjectAssoc
	 * @return the response
	 */
	Response savePolicyAssoc(PolicyObjectAssoc poa);

    ITPolicy findITPolicy();

    Response resetITPolicy();

    Response saveOrUpdateITPolicy(ITPolicy itPolicy);

}
