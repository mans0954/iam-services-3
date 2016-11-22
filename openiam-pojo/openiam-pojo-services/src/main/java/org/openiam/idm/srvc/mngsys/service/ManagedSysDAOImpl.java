package org.openiam.idm.srvc.mngsys.service;

// Generated Nov 3, 2008 12:14:44 AM by Hibernate Tools 3.2.2.GA

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.ManagedSysSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Home object for domain model class ManagedSys.
 * @see org.openiam.idm.srvc.mngsys.service
 * @author Hibernate Tools
 */
@Repository("managedSysDAO")
public class ManagedSysDAOImpl extends BaseDaoImpl<ManagedSysEntity, String> implements ManagedSysDAO {

	
	
	
    @Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		final Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof ManagedSysSearchBean) {
			final ManagedSysSearchBean sb = (ManagedSysSearchBean)searchBean;
			if(CollectionUtils.isNotEmpty(sb.getKeySet())) {
                criteria.add(Restrictions.in(getPKfieldName(), sb.getKeySet()));
            } else {
				final Criterion nameCriterion = getStringCriterion("name", sb.getNameToken(), sysConfig.isCaseInSensitiveDatabase());
                if(nameCriterion != null) {
                	criteria.add(nameCriterion);
                }

				if(StringUtils.isNotBlank(sb.getResourceId())){
					criteria.add(Restrictions.eq("resource.id", sb.getResourceId()));
				}
			}
		}
		return criteria;
	}

	@SuppressWarnings(value = "unchecked")
    @Override
	public List<ManagedSysEntity> findbyConnectorId(String connectorId) {
		Criteria criteria = getCriteria().add(Restrictions.eq("connectorId",connectorId)).addOrder(Order.asc(getPKfieldName()));
		return (List<ManagedSysEntity>)criteria.list();
	}

//	@SuppressWarnings(value = "unchecked")
//	public List<ManagedSysEntity> findbyDomain(String domainId) {
//        Criteria criteria = getCriteria().add(Restrictions.eq("domainId",domainId)).addOrder(Order.asc(getPKfieldName()));
//		return (List<ManagedSysEntity>)criteria.list();
//	}

    @Override
    @SuppressWarnings(value = "unchecked")
	public List<ManagedSysEntity> findAllManagedSys() {
        Criteria criteria = getCriteria().addOrder(Order.asc("name"));
        return (List<ManagedSysEntity>)criteria.list();
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.mngsys.service.ManagedSysDAO#findByName(java.lang.String)
	 */
    @Override
    @SuppressWarnings(value = "unchecked")
    @Deprecated
    public ManagedSysEntity findByName(String name) {
        Criteria criteria = getCriteria().add(Restrictions.eq("name",name)).addOrder(Order.asc("name")).addOrder(Order.asc(getPKfieldName()));
        List<ManagedSysEntity> results = (List<ManagedSysEntity>)criteria.list();
        if(CollectionUtils.isNotEmpty(results)) {
            log.info("ManagedSys resultSet = " + results.size());
            return results.get(0);
        } else {
            log.info("No managedSys objects found. [findByName]");
            return null;
        }
	}

    @Override
    @SuppressWarnings(value = "unchecked")
    @Deprecated
    public ManagedSysEntity findByResource(String resourceId, String status) {
        Criteria criteria = getCriteria()
                .add(Restrictions.eq("resource.id",resourceId))
                .add(Restrictions.eq("status",status))
                .addOrder(Order.asc("name"));

        List<ManagedSysEntity> results = (List<ManagedSysEntity>)criteria.list();

		if (CollectionUtils.isNotEmpty(results)) {
			// avoids an exception in the event that there is more than 1 row with the same name
			log.info("ManagedSys resultSet = " + results.size());	
			return results.get(0);
		}
		log.info("No managedSys objects fround. [findByResource]");
		return null;
	
	}

    @Override
    @SuppressWarnings(value = "unchecked")
    @Deprecated
    public String findIdByResource(String resourceId, String status) {
        Criteria criteria = getCriteria()
                .add(Restrictions.eq("resourceId",resourceId))
                .add(Restrictions.eq("status",status))
                .setProjection(Projections.id());

        List<String> results = (List<String>)criteria.list();

        if (CollectionUtils.isNotEmpty(results)) {
            // avoids an exception in the event that there is more than 1 row with the same name
            log.info("ManagedSys resultSet = " + results.size());
            return results.get(0);
        }
        log.info("No managedSys objects fround.[findIdByResource]");
        return null;

    }

    @Override
    protected String getPKfieldName() {
        return "id";
    }

	@Override
	public List<ManagedSysEntity> findByResource(String resourceId) {
		 return getCriteria()
	                .add(Restrictions.eq("resource.id",resourceId))
	                .addOrder(Order.asc("name")).list();
	}
}
