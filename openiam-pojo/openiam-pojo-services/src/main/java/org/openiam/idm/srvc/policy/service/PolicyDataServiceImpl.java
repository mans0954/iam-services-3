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
import javax.jws.WebService;
import org.apache.commons.collections.CollectionUtils;
import org.openiam.dozer.converter.PolicyAttributeDozerConverter;
import org.openiam.dozer.converter.PolicyDefDozerConverter;
import org.openiam.dozer.converter.PolicyDefParamDozerConverter;
import org.openiam.dozer.converter.PolicyDozerConverter;
import org.openiam.dozer.converter.PolicyObjectAssocDozerConverter;
import org.openiam.idm.srvc.policy.domain.PolicyDefEntity;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.domain.PolicyObjectAssocEntity;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyDef;
import org.openiam.idm.srvc.policy.dto.PolicyDefParam;
import org.openiam.idm.srvc.policy.dto.PolicyObjectAssoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	@Autowired
	private PolicyDefDAO policyDefDao;
	@Autowired
	private PolicyDAO policyDao;
	@Autowired
	private PolicyDefParamDAO policyDefParamDao;
	@Autowired
	private PolicyObjectAssocDAO policyObjectAssocDAO;
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

	public Policy getPolicy(String policyId) {
		if (policyId == null) {
			throw new NullPointerException("PolicyId is null");
		}
		PolicyEntity p = policyDao.findById(policyId);
		if (p == null)
			return null;
		return policyDozerConverter.convertToDTO(p, true);
	}

	public PolicyDefParamDAO getPolicyDefParamDao() {
		return policyDefParamDao;
	}

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
}
