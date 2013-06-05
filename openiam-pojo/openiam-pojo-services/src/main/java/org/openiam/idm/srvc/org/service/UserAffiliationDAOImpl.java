package org.openiam.idm.srvc.org.service;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.UserAffiliationEntity;
import org.springframework.stereotype.Repository;


import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.util.List;

@Repository("orgAffiliationDAO")
public class UserAffiliationDAOImpl extends BaseDaoImpl<UserAffiliationEntity, String> implements UserAffiliationDAO {

	private static String DELETE_BY_ORGANIZATION_ID = "DELETE FROM %s ua WHERE ua.organization.orgId = :organizationId";
	
	@PostConstruct
	public void initSQL() {
		DELETE_BY_ORGANIZATION_ID = String.format(DELETE_BY_ORGANIZATION_ID, domainClass.getSimpleName());
	}
	
	
	@Override
	public List<OrganizationEntity> findOrgAffiliationsByUser(String userId) {
		Session session = getSession();

		Query qry = session.createQuery("select org from org.openiam.idm.srvc.org.domain.OrganizationEntity as org, org.openiam.idm.srvc.org.domain.UserAffiliationEntity ua " +
						" where ua.user.userId = :userId and ua.organization.orgId = org.orgId " +
						" order by org.organizationName ");
		
		qry.setString("userId",userId);

		List<OrganizationEntity> result = (List<OrganizationEntity>)qry.list();
		if (result == null || result.size() == 0)
			return null;
		return result;			
	}
	
	@Override
	public UserAffiliationEntity getRecord(String userId, String organizationId) {
		final Criteria criteria = getCriteria()
								.add(Restrictions.eq("user.userId", userId))
								.add(Restrictions.eq("organization.orgId", organizationId));
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
		return "userAffiliationId";
	}
}
