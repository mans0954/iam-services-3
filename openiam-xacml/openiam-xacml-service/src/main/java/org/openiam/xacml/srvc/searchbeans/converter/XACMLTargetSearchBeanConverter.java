package org.openiam.xacml.srvc.searchbeans.converter;

import org.openiam.idm.searchbeans.xacml.XACMLTargetSearchBean;
import org.openiam.idm.srvc.searchbean.converter.SearchBeanConverter;
import org.openiam.xacml.srvc.domain.XACMLTargetEntity;
import org.springframework.stereotype.Component;

@Component("xacmlTargetSearchBeanConverter")
public class XACMLTargetSearchBeanConverter implements
        SearchBeanConverter<XACMLTargetEntity, XACMLTargetSearchBean> {
    @Override
    public XACMLTargetEntity convert(XACMLTargetSearchBean searchBean) {
        final XACMLTargetEntity entity = new XACMLTargetEntity();
        if (searchBean != null) {
            entity.setId(searchBean.getKey());
            entity.setName(searchBean.getName());
        }
        return entity;
    }
}
