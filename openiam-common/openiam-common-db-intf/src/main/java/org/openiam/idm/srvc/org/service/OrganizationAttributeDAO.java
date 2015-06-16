package org.openiam.idm.srvc.org.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;

import java.util.List;

public interface OrganizationAttributeDAO extends BaseDao<OrganizationAttributeEntity, String> {

	public void deleteByOrganizationId(final String organizationId);

	public List<OrganizationAttributeEntity> findOrgAttributes(final String organizationId);
}
