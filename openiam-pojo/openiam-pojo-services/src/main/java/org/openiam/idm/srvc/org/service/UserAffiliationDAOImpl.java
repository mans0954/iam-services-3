package org.openiam.idm.srvc.org.service;


import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.UserAffiliationEntity;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

@Repository("orgAffiliationDAO")
public class UserAffiliationDAOImpl extends BaseDaoImpl<UserAffiliationEntity, String> implements UserAffiliationDAO {

	private static String DELETE_BY_ORGANIZATION_ID = "DELETE FROM %s ua WHERE ua.organization.orgId = :organizationId";
	
	@PostConstruct
	public void initSQL() {
		DELETE_BY_ORGANIZATION_ID = String.format(DELETE_BY_ORGANIZATION_ID, domainClass.getSimpleName());
	}
	
	
	@Override
	public List<OrganizationEntity> findOrgAffiliationsByUser(String userId, Set<String> filter) {
		Session session = getSession();



		Query qry = session.createQuery("select org from org.openiam.idm.srvc.org.domain.OrganizationEntity as or, org.openiam.idm.srvc.org.domain.UserAffiliationEntity ua " +
						" where ua.user.userId = :userId and ua.organization.orgId = or.orgId " +
                        ((filter!=null && !filter.isEmpty())? " and or.orgId in (:orgList)" :"") +
						" order by or.organizationName ");
		
		qry.setString("userId",userId);
        if(filter!=null && !filter.isEmpty()){
            qry.setParameterList("orgList", filter);
        }


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
