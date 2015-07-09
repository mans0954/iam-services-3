package org.openiam.xacml.srvc.service;

import org.openiam.xacml.srvc.domain.XACMLPolicyEntity;
import org.openiam.xacml.srvc.searchbeans.XACMLPolicySearchBean;

import java.util.List;

/**
 * Created by zaporozhec on 7/10/15.
 */
public interface AbstractXACMLBeansService<Entity, SearchBean> extends AbstractXACMLService<Entity> {

    public List<Entity> findBeans(SearchBean policySearchBean, int from, int size) ;

    public List<Entity> findBeans(SearchBean policySearchBean) ;
}
