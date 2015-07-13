package org.openiam.xacml.srvc.service;

import org.openiam.xacml.srvc.domain.XACMLPolicyEntity;
import org.openiam.xacml.srvc.searchbeans.XACMLPolicySearchBean;

import java.util.List;

/**
 * Created by zaporozhec on 7/14/15.
 */
public abstract class XACMLPolicyService {
    public abstract XACMLPolicyEntity add(XACMLPolicyEntity policyEntity) throws Exception;

    public abstract XACMLPolicyEntity update(XACMLPolicyEntity policyEntity) throws Exception;

    public abstract List<XACMLPolicyEntity> findAll() throws Exception;

    public abstract XACMLPolicyEntity findById(String id) throws Exception;

    public abstract void delete(String id) throws Exception;

    public abstract List<XACMLPolicyEntity> findBeans(XACMLPolicySearchBean policySearchBean, int from, int size) throws Exception;
}
