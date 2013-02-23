package org.openiam.idm.srvc.meta.service;

import java.util.List;
import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;

/**
 * DAO Interface for MetadataType
 *
 * @author suneet
 */
public interface MetadataTypeDAO extends BaseDao<MetadataTypeEntity, String> {

    void addCategoryToType(String typeId, String categoryId);

    void removeCategoryFromType(String typeId, String categoryId);

    List<MetadataTypeEntity> findTypesInCategory(String categoryId);

}
