package org.openiam.xacml.srvc.rest;

import java.util.List;

/**
 * Created by zaporozhec on 7/10/15.
 */
public interface AbstractXACMLBeansRestService<DTO, SearchBean> extends AbstractXACMLRestService<DTO> {

    public List<DTO> findBeans(SearchBean policySearchBean, int from, int size) ;

    public List<DTO> findBeans(SearchBean policySearchBean) ;
}
