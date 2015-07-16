package org.openiam.xacml.srvc.searchbeans.converter;

import org.openiam.idm.searchbeans.xacml.XACMLPolicySetSearchBean;
import org.openiam.idm.srvc.searchbean.converter.SearchBeanConverter;
import org.openiam.xacml.srvc.domain.XACMLPolicySetEntity;
import org.springframework.stereotype.Component;

@Component("xacmlPolicySetSearchBeanConverter")
public class XACMLPolicySetSearchBeanConverter implements
        SearchBeanConverter<XACMLPolicySetEntity, XACMLPolicySetSearchBean> {
    @Override
    public XACMLPolicySetEntity convert(XACMLPolicySetSearchBean searchBean) {
        final XACMLPolicySetEntity entity = new XACMLPolicySetEntity();
        if (searchBean != null) {
            entity.setId(searchBean.getKey());
            entity.setIdentifier(searchBean.getIdentifier());
            entity.setIssuer(searchBean.getIssuer());
            entity.setPolicySetDefaults(searchBean.getPolicySetDefaults());
            entity.setVersion(searchBean.getVersion());
        }
        return entity;
    }
}
