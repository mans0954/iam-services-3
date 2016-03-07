package org.openiam.idm.srvc.mngsys.searchbeans.converter;

import org.openiam.idm.searchbeans.ManagedSysSearchBean;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.searchbean.converter.SearchBeanConverter;
import org.springframework.stereotype.Component;

@Component("managedSysSearchBeanConverter")
public class ManagedSystemSearchBeanConverter implements SearchBeanConverter<ManagedSysEntity, ManagedSysSearchBean> {

    @Override
    public ManagedSysEntity convert(ManagedSysSearchBean searchBean) {
        ManagedSysEntity managedSysEntity = new ManagedSysEntity();
        managedSysEntity.setId(searchBean.getKey());
        managedSysEntity.setName(searchBean.getName());
        return managedSysEntity;
    }
}
