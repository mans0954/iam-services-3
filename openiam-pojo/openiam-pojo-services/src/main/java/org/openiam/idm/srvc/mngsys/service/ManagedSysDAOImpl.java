package org.openiam.idm.srvc.mngsys.service;

// Generated Nov 3, 2008 12:14:44 AM by Hibernate Tools 3.2.2.GA

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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

    @SuppressWarnings(value = "unchecked")
	public List<ManagedSysEntity> findbyConnectorId(String connectorId) {
		Criteria criteria = getCriteria().add(Restrictions.eq("connectorId",connectorId)).addOrder(Order.asc("managedSysId"));
		return (List<ManagedSysEntity>)criteria.list();
	}

	@SuppressWarnings(value = "unchecked")
	public List<ManagedSysEntity> findbyDomain(String domainId) {
        Criteria criteria = getCriteria().add(Restrictions.eq("domainId",domainId)).addOrder(Order.asc("managedSysId"));
		return (List<ManagedSysEntity>)criteria.list();
	}

    @SuppressWarnings(value = "unchecked")
	public List<ManagedSysEntity> findAllManagedSys() {
        Criteria criteria = getCriteria().addOrder(Order.asc("name"));
        return (List<ManagedSysEntity>)criteria.list();
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.mngsys.service.ManagedSysDAO#findByName(java.lang.String)
	 */
    @SuppressWarnings(value = "unchecked")
    public ManagedSysEntity findByName(String name) {
        Criteria criteria = getCriteria().add(Restrictions.eq("name",name)).addOrder(Order.asc("name")).addOrder(Order.asc("managedSysId"));
        List<ManagedSysEntity> results = (List<ManagedSysEntity>)criteria.list();
        if(CollectionUtils.isNotEmpty(results)) {
            log.info("ManagedSys resultSet = " + results.size());
            return results.get(0);
        } else {
            log.info("No managedSys objects fround.");
            return null;
        }
	}

    @SuppressWarnings(value = "unchecked")
    public ManagedSysEntity findByResource(String resourceId, String status) {
        Criteria criteria = getCriteria()
                .add(Restrictions.eq("resourceId",resourceId))
                .add(Restrictions.eq("status",status))
                .addOrder(Order.asc("name"));

        List<ManagedSysEntity> results = (List<ManagedSysEntity>)criteria.list();

		if (CollectionUtils.isNotEmpty(results)) {
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
