package org.openiam.idm.srvc.synch.searchbeans.converter;

import org.openiam.idm.srvc.searchbean.converter.SearchBeanConverter;
import org.openiam.idm.srvc.synch.domain.SynchConfigEntity;
import org.openiam.idm.srvc.synch.dto.SynchConfigSearchBean;
import org.springframework.stereotype.Component;

@Component("synchConfigSearchBeanConverter")
public class SynchConfigSearchBeanConverter implements SearchBeanConverter<SynchConfigEntity, SynchConfigSearchBean> {

    @Override
    public SynchConfigEntity convert(SynchConfigSearchBean searchBean) {
        SynchConfigEntity entity = new SynchConfigEntity();
        entity.setSynchConfigId(searchBean.getKey());
        entity.setName(searchBean.getName());
        entity.setSynchType(searchBean.getSynchType());
        return entity;
    }
}
