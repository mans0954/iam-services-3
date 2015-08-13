package org.openiam.idm.srvc.org.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.org.domain.OrgType2OrgTypeXrefEntity;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.openiam.idm.srvc.org.dto.OrgType2OrgTypeXref;

import java.util.List;

public interface OrganizationTypeDAO extends BaseDao<OrganizationTypeEntity, String> {
    List<OrgType2OrgTypeXrefEntity> getOrgTypeToOrgTypeXrefList();

    List<String> findAllIds();
}
