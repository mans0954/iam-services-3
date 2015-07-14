package org.openiam.xacml.srvc.service;

import org.openiam.idm.searchbeans.xacml.XACMLPolicySearchBean;
import org.openiam.idm.searchbeans.xacml.XACMLTargetSearchBean;
import org.openiam.xacml.srvc.domain.XACMLTargetEntity;

import java.util.List;

/**
 * Created by zaporozhec on 7/14/15.
 */
public interface XACMLTargetService {
    public XACMLTargetEntity add(XACMLTargetEntity policyEntity) throws Exception;

    public XACMLTargetEntity update(XACMLTargetEntity policyEntity) throws Exception;

    public List<XACMLTargetEntity> findAll() throws Exception;

    public XACMLTargetEntity findById(String id) throws Exception;

    public void delete(String id) throws Exception;

    public List<XACMLTargetEntity> findBeans(XACMLTargetSearchBean policySearchBean, int from, int size) throws Exception;
}
