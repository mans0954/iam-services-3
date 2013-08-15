package org.openiam.idm.srvc.org.service;


import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.UserAffiliationEntity;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository("orgAffiliationDAO")
public class UserAffiliationDAOImpl extends BaseDaoImpl<UserAffiliationEntity, String> implements UserAffiliationDAO {

	private static String DELETE_BY_ORGANIZATION_ID = "DELETE FROM %s ua WHERE ua.organization.id = :organizationId";
	
	@PostConstruct
	public void initSQL() {
		DELETE_BY_ORGANIZATION_ID = String.format(DELETE_BY_ORGANIZATION_ID, domainClass.getSimpleName());
	}
	
	@Override
	public UserAffiliationEntity getRecord(String userId, String organizationId) {
		final Criteria criteria = getCriteria()
								.add(Restrictions.eq("user.userId", userId))
								.add(Restrictions.eq("organization.id", organizationId));
		return (UserAffiliationEntity)criteria.uniqueResult();
	}
	
	@Override
	public void deleteByOrganizationId(String organizationId) {
		final Query query = getSession().createQuery(DELETE_BY_ORGANIZATION_ID);
		query.setParameter("organizationId", organizationId);
		query.executeUpdate();
	}

	@Override
	protected String getPKfieldName() {
		return "id";
	}

	@Override
	public List<String> getUserIdsInOrganization(final Collection<String> organizationIds, int from, int size) {
		final Criteria criteria = getCriteria().add(Restrictions.in("organization.id", organizationIds)).setProjection(Projections.property("user.userId"));
		
		if(from > -1) {
			criteria.setFirstResult(from);
		}

		if(size > 0) {
			criteria.setMaxResults(size);
		}
		
		return criteria.list();
	}
}
