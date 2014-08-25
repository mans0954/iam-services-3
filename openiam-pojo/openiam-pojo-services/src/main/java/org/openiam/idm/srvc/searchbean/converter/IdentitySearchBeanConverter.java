package org.openiam.idm.srvc.searchbean.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.IdentitySearchBean;
import org.openiam.idm.srvc.auth.domain.IdentityEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Component;

@Component("identitySearchBeanConverter")
public class IdentitySearchBeanConverter implements SearchBeanConverter<IdentityEntity, IdentitySearchBean> {

    @Override
    public IdentityEntity convert(IdentitySearchBean searchBean) {
        final IdentityEntity identityEntity = new IdentityEntity();
        identityEntity.setIdentity(searchBean.getIdentity());

        if(StringUtils.isNotBlank(searchBean.getManagedSysId())) {
            identityEntity.setManagedSysId(searchBean.getManagedSysId());
        }
        if(StringUtils.isNotBlank(searchBean.getReferredObjectId())) {
            identityEntity.setReferredObjectId(searchBean.getReferredObjectId());
        }
        if(StringUtils.isNotBlank(searchBean.getCreatedBy())) {
            identityEntity.setCreatedBy(searchBean.getCreatedBy());
        }
        if(searchBean.getStatus() != null) {
            identityEntity.setStatus(searchBean.getStatus());
        }
        if(searchBean.getType() != null) {
            identityEntity.setType(searchBean.getType());
        }
        return identityEntity;
    }
}
