package org.openiam.idm.srvc.policy.service;

import java.util.List;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.srvc.policy.dto.ITPolicy;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyDefParam;

public interface PolicyService {

    Policy getPolicy(final String policyId) throws BasicDataServiceException;

    void save(final Policy policy);

    void delete(final String policyId) throws BasicDataServiceException;

    int count(PolicySearchBean searchBean);

    List<Policy> findBeans(final PolicySearchBean searchBean, int from, int size);

    List<PolicyDefParam> findPolicyDefParamByGroup(final String policyDefId, final String pswdGroup) throws BasicDataServiceException;

    ITPolicy findITPolicy();

    void resetITPolicy();

    void saveITPolicy(ITPolicy itPolicy) throws BasicDataServiceException;
}
