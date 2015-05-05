package org.openiam.idm.srvc.meta.service;

import java.util.List;
import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;

/**
 * DAO Interface for MetadataType
 *
 * @author suneet
 */
public interface MetadataTypeDAO extends BaseDao<MetadataTypeEntity, String> {

//    public List<MetadataTypeEntity> findTypesInCategory(String categoryId);
    public MetadataTypeEntity findByNameGrouping(String name, MetadataTypeGrouping grouping);
}
