package org.openiam.idm.srvc.mngsys.service;

import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.openiam.core.dao.BaseDao;

import java.util.List;

public interface ProvisionConnectorDao extends BaseDao<ProvisionConnectorEntity, String> {
    List<MetadataTypeEntity> getMetadataTypes();
}
