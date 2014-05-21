package org.openiam.idm.srvc.policy.service;

import java.util.List;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.srvc.policy.domain.PolicyDefParamEntity;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.dto.Policy;

public interface PolicyService {

	public PolicyEntity getPolicy(final String policyId);
	public void save(final PolicyEntity entity);
	public List<PolicyEntity> findPolicyByName(final String policyDefId, final String policyName);
	public void delete(final String policyId);
	public int count(PolicySearchBean searchBean);
	public List<PolicyEntity> findBeans(final PolicySearchBean searchBean, int from, int size);
	public List<PolicyDefParamEntity> findPolicyDefParamByGroup(final String policyDefId, final String pswdGroup);
}
