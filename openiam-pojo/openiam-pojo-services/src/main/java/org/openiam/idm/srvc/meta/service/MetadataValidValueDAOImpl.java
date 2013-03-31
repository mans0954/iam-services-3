package org.openiam.idm.srvc.meta.service;

import javax.annotation.PostConstruct;

import org.hibernate.Query;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.meta.domain.MetadataValidValueEntity;
import org.springframework.stereotype.Repository;

@Repository("metadataValidValueDAO")
public class MetadataValidValueDAOImpl extends BaseDaoImpl<MetadataValidValueEntity, String> implements MetadataValidValueDAO {

	private static String DELETE_BY_META_ELEMENT_ID = "DELETE FROM %s v WHERE v.entity.id = :metaElementId";
	
	@PostConstruct
	public void initSQL() {
		DELETE_BY_META_ELEMENT_ID = String.format(DELETE_BY_META_ELEMENT_ID, domainClass.getSimpleName());
	}
	
	@Override
	protected String getPKfieldName() {
		return "id";
	}

	@Override
	public void deleteByMetaElementId(String metaElementId) {
		final Query query = getSession().createQuery(DELETE_BY_META_ELEMENT_ID);
		query.setParameter("metaElementId", metaElementId);
		query.executeUpdate();
	}

}
