package org.openiam.idm.srvc.searchbean.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.ResourceTypeSearchBean;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.springframework.stereotype.Component;

@Component
public class ResourceTypeSearchBeanConverter implements SearchBeanConverter<ResourceTypeEntity, ResourceTypeSearchBean> {

    @Override
    public ResourceTypeEntity convert(final ResourceTypeSearchBean searchBean) {
        final ResourceTypeEntity entity = new ResourceTypeEntity();
        if (searchBean != null) {
            entity.setId(StringUtils.trimToNull(searchBean.getKey()));
            if (searchBean.isSearchable() == null) {
                entity.setSelectAll(true);
            } else {
                entity.setSearchable(searchBean.isSearchable());
                entity.setSelectAll(false);
            }
            entity.setDescription(searchBean.getDescription());
        }
        return entity;
    }

}
