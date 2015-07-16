package org.openiam.xacml.srvc.service;

import org.openiam.idm.searchbeans.xacml.XACMLPolicySetSearchBean;
import org.openiam.xacml.srvc.domain.XACMLPolicySetEntity;

import java.util.List;

/**
 * Created by zaporozhec on 7/14/15.
 */
public interface XACMLPolicySetService {
    public XACMLPolicySetEntity add(XACMLPolicySetEntity policyEntity) throws Exception;

    public XACMLPolicySetEntity update(XACMLPolicySetEntity policyEntity) throws Exception;

    public List<XACMLPolicySetEntity> findAll() throws Exception;

    public XACMLPolicySetEntity findById(String id) throws Exception;

    public void delete(String id) throws Exception;

    public List<XACMLPolicySetEntity> findBeans(XACMLPolicySetSearchBean policySearchBean, int from, int size) throws Exception;
}
