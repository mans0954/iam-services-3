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

import java.util.List;
import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;

/**
 * Data access implementation for Policy
 * 
 * @author suneet
 */
public interface PolicyDAO extends BaseDao<PolicyEntity, String> {

	/**
	 * Find all policies.
	 *
	 * @param policyDefId the policy def id
	 * @return the list
	 */
	public List<PolicyEntity> findAllPolicies(String policyDefId);

	/**
	 * Find policy by name.
	 *
	 * @param policyDefId the policy def id
	 * @param policyName the policy name
	 * @return the list
	 */
	public List<PolicyEntity> findPolicyByName(String policyDefId,
			String policyName);

}