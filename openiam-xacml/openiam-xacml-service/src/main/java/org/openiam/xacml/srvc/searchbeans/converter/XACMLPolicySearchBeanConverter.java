package org.openiam.xacml.srvc.searchbeans.converter;

import org.openiam.idm.srvc.searchbean.converter.SearchBeanConverter;
import org.openiam.xacml.srvc.domain.XACMLPolicyEntity;
import org.openiam.xacml.srvc.searchbeans.XACMLPolicySearchBean;
import org.springframework.stereotype.Component;

@Component("xacmlPolicySearchBeanConverter")
public class XACMLPolicySearchBeanConverter implements
        SearchBeanConverter<XACMLPolicyEntity, XACMLPolicySearchBean> {
    @Override
    public XACMLPolicyEntity convert(XACMLPolicySearchBean searchBean) {
        final XACMLPolicyEntity entity = new XACMLPolicyEntity();
        if (searchBean != null) {
            entity.setId(searchBean.getKey());
            entity.setIdentifier(searchBean.getIdentifier());
        }
        return entity;
    }
}
