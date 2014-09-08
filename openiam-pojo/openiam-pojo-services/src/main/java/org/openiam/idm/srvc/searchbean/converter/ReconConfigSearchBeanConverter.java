package org.openiam.idm.srvc.searchbean.converter;

import org.openiam.idm.searchbeans.ReconConfigSearchBean;
import org.openiam.idm.srvc.recon.domain.ReconciliationConfigEntity;
import org.springframework.stereotype.Component;

@Component("reconConfigSearchBeanConverter")
public class ReconConfigSearchBeanConverter  implements SearchBeanConverter<ReconciliationConfigEntity, ReconConfigSearchBean> {
    @Override
    public ReconciliationConfigEntity convert(ReconConfigSearchBean searchBean) {
        final ReconciliationConfigEntity resource = new ReconciliationConfigEntity();
        resource.setReconConfigId(searchBean.getKey());
        resource.setName(searchBean.getName());
        resource.setReconType(searchBean.getReconType());
        resource.setResourceId(searchBean.getResourceId());
        resource.setManagedSysId(searchBean.getManagedSysId());
        return resource;
    }
}
