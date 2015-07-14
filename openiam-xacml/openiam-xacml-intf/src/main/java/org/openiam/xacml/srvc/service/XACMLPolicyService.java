package org.openiam.xacml.srvc.service;

import org.openiam.xacml.srvc.domain.XACMLPolicyEntity;
import org.openiam.idm.searchbeans.xacml.XACMLPolicySearchBean;

import java.util.List;

/**
 * Created by zaporozhec on 7/14/15.
 */
public interface XACMLPolicyService {
    public XACMLPolicyEntity add(XACMLPolicyEntity policyEntity) throws Exception;

    public XACMLPolicyEntity update(XACMLPolicyEntity policyEntity) throws Exception;

    public List<XACMLPolicyEntity> findAll() throws Exception;

    public XACMLPolicyEntity findById(String id) throws Exception;

    public void delete(String id) throws Exception;

    public List<XACMLPolicyEntity> findBeans(XACMLPolicySearchBean policySearchBean, int from, int size) throws Exception;
}
