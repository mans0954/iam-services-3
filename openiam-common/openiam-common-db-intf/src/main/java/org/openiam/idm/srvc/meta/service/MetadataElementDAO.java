package org.openiam.idm.srvc.meta.service;

import java.util.List;
import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;

/**
 * DAO Interface for MetadataElement
 *
 * @author suneet
 */
public interface MetadataElementDAO extends BaseDao<MetadataElementEntity, String> {

    public MetadataElementEntity findByAttrNameTypeId(String attrName, String typeId);

    public String findIdByAttrNameTypeId(String attrName, String typeId);

    public List<MetadataElementEntity> getByResourceId(final String resourceId);
}