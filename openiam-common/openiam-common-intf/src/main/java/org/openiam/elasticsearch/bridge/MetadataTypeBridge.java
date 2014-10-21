package org.openiam.elasticsearch.bridge;

import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;

/**
 * Created by alexander on 22.10.14.
 */
public class MetadataTypeBridge implements ElasticsearchBrigde {
    @Override
    public String objectToString(Object object) {
        String retVal = null;
        if(object instanceof MetadataTypeEntity) {
            retVal = ((MetadataTypeEntity)object).getId();
        }
        return retVal;
    }

    @Override
    public Object stringToObject(String stringValue) {
        final MetadataTypeEntity entity = new MetadataTypeEntity();
        entity.setId(stringValue);
        return entity;
    }
}
