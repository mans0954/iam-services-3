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


import javax.naming.InitialContext;
import java.util.List;

@Repository("orgAffiliationDAO")
public class UserAffiliationDAOImpl extends BaseDaoImpl<UserAffiliationEntity, String> implements UserAffiliationDAO {

	
	
	@Override
	public List<OrganizationEntity> findOrgAffiliationsByUser(String userId) {
		Session session = sessionFactory.getCurrentSession();

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
	public void removeUserFromOrg(String orgId, String userId) {

		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("delete org.openiam.idm.srvc.org.domain.UserAffiliationEntity ur " +
					" where  ur.organization.orgId = :orgId and ur.user.userId = :userId ");
		qry.setString("orgId", orgId);
		qry.setString("userId", userId);
		qry.executeUpdate();	
	}
	
	@Override
	public UserAffiliationEntity getRecord(String userId, String organizationId) {
		final Criteria criteria = getCriteria()
								.add(Restrictions.eq("user.userId", userId))
								.add(Restrictions.eq("organization.orgId", organizationId));
		return (UserAffiliationEntity)criteria.uniqueResult();
	}

	@Override
	protected String getPKfieldName() {
		return "userAffiliationId";
	}
}
