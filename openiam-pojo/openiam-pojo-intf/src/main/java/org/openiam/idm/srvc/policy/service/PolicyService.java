package org.openiam.idm.srvc.policy.service;

import java.util.List;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.srvc.policy.dto.ITPolicy;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyDefParam;

public interface PolicyService {

	public Policy getPolicy(final String policyId);
	public void save(final Policy policy);
	public List<Policy> findPolicyByName(final String policyDefId, final String policyName);
	public void delete(final String policyId)  throws BasicDataServiceException;
	public int count(PolicySearchBean searchBean);
	public List<Policy> findBeans(final PolicySearchBean searchBean, int from, int size);
	public List<PolicyDefParam> findPolicyDefParamByGroup(final String policyDefId, final String pswdGroup);
    ITPolicy findITPolicy();
    void resetITPolicy();
    void saveITPolicy(ITPolicy itPolicy);
}
