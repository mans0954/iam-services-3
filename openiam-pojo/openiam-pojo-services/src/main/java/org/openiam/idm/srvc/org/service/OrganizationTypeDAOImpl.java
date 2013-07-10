package org.openiam.idm.srvc.org.service;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.openiam.idm.srvc.org.dto.OrganizationType;
import org.springframework.stereotype.Repository;

@Repository
public class OrganizationTypeDAOImpl extends BaseDaoImpl<OrganizationTypeEntity, String> implements OrganizationTypeDAO {

	@Override
	protected Criteria getExampleCriteria(OrganizationTypeEntity entity) {
		final Criteria criteria = getCriteria();
		if(entity != null) {
			if(StringUtils.isNotBlank(entity.getId())) {
				criteria.add(Restrictions.eq("id", entity.getId()));
			} else {
				if(StringUtils.isNotBlank(entity.getName())) {
					criteria.add(Restrictions.eq("name", entity.getId()));
				}
			}
		}
		return criteria;
	}

	@Override
	protected String getPKfieldName() {
		return "id";
	}

}
