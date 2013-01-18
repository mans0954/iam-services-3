package org.openiam.idm.srvc.mngsys.service;

// Generated Nov 3, 2008 12:14:44 AM by Hibernate Tools 3.2.2.GA

import org.hibernate.Query;
import org.hibernate.Session;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class ManagedSys.
 * @see org.openiam.idm.srvc.mngsys.service
 * @author Hibernate Tools
 */
@Repository("managedSysDAO")
public class ManagedSysDAOImpl extends BaseDaoImpl<ManagedSysEntity, String> implements ManagedSysDAO {

	public List<ManagedSysEntity> findbyConnectorId(String connectorId) {
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("from " +  ManagedSysEntity.class.getName()+
				" ms where ms.connectorId = :conId order by ms.managedSysId asc");
		qry.setString("conId", connectorId);
		List<ManagedSysEntity> results = (List<ManagedSysEntity>)qry.list();
		return results;	
	}
	
	public List<ManagedSysEntity> findbyDomain(String domainId) {
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("from " +  ManagedSysEntity.class.getName()+
				" ms where ms.domainId = :domainId order by ms.managedSysId asc");
		qry.setString("domainId", domainId);
		List<ManagedSysEntity> results = (List<ManagedSysEntity>)qry.list();
		return results;			
	}
	
	 public List<ManagedSysEntity> findAllManagedSys() {
			Session session = sessionFactory.getCurrentSession();
			Query qry = session.createQuery("from " +  ManagedSysEntity.class.getName()+
					" ms order by ms.name asc");
			List<ManagedSysEntity> results = (List<ManagedSysEntity>)qry.list();
			return results;				 
	 }

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.mngsys.service.ManagedSysDAO#findByName(java.lang.String)
	 */
	public ManagedSysEntity findByName(String name) {
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("from " +  ManagedSysEntity.class.getName()+
				" ms where ms.name = :name order by ms.name, ms.managedSysId ");
		qry.setString("name", name);
		List<ManagedSysEntity> results = (List<ManagedSysEntity>)qry.list();

		if (results != null) {
			// avoids an exception in the event that there is more than 1 row with the same name
			log.info("ManagedSys resultSet = " + results.size());	
			return results.get(0);
		}
		log.info("No managedSys objects fround.");
		return null;
	
	}

	public ManagedSysEntity findByResource(String resourceId, String status) {
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("from " +  ManagedSysEntity.class.getName()+
				" ms where ms.resourceId = :resourceId and  " +
				"		ms.status = :status " +
				" order by ms.name ");
		qry.setString("resourceId", resourceId);
		qry.setString("status",status);
		List<ManagedSysEntity> results = (List<ManagedSysEntity>)qry.list();

		if (results != null) {
			// avoids an exception in the event that there is more than 1 row with the same name
			log.info("ManagedSys resultSet = " + results.size());	
			return results.get(0);
		}
		log.info("No managedSys objects fround.");
		return null;
	
	}
	
    @Override
    protected String getPKfieldName() {
        return "managedSysId";
    }
}
