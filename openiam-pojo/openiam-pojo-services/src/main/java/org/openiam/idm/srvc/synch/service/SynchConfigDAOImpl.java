package org.openiam.idm.srvc.synch.service;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.synch.domain.SynchConfigEntity;
import org.openiam.idm.srvc.synch.dto.SynchConfigSearchBean;
import org.springframework.stereotype.Repository;

/**
 * Home object for domain model class SynchConfig.
 */
@Repository("synchConfigDAO")
public class SynchConfigDAOImpl extends BaseDaoImpl<SynchConfigEntity, String> implements SynchConfigDAO {

	private static final Log log = LogFactory.getLog(SynchConfigDAOImpl.class);

	public SynchConfigEntity add(SynchConfigEntity transientInstance) {
		try {
            getSession().persist(transientInstance);
			return transientInstance;
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(SynchConfigEntity persistentInstance) {
		try {
            getSession().delete(persistentInstance);
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public SynchConfigEntity merge(SynchConfigEntity detachedInstance) {
		try {
            SynchConfigEntity ret = (SynchConfigEntity) getSession().merge(detachedInstance);
            return ret;

		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public SynchConfigEntity findById(java.lang.String id) {

		try {
            SynchConfigEntity instance = (SynchConfigEntity)getCriteria()
                    .add(Restrictions.idEq(id)).uniqueResult();
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.synch.service.SynchConfigDAO#findAllConfig()
	 */
	public List<SynchConfigEntity> findAllConfig() {
        Criteria criteria = getCriteria().addOrder(Order.asc("name"));
        return (List<SynchConfigEntity>)criteria.list();
	}

	public int updateExecTime(String configId, Timestamp execTime) {
		Query qry = getSession().createQuery("UPDATE org.openiam.idm.srvc.synch.domain.SynchConfigEntity sc " +
                " 					SET  sc.lastExecTime = :execTime  " +
                " 					WHERE  sc.id = :configId	");
		
		qry.setTimestamp("execTime", execTime);
		qry.setString("configId", configId);
		
		return qry.executeUpdate();
	}

    public int updateLastRecProcessed(String configId,String processTime) {
		Query qry = getSession().createQuery(" UPDATE org.openiam.idm.srvc.synch.domain.SynchConfigEntity sc " +
                " 					SET  sc.lastRecProcessed = :processTime  " +
                " 					WHERE  sc.id = :configId	");

		qry.setString("processTime",processTime);
		qry.setString("configId", configId);

		return qry.executeUpdate();
	}

    @Override
    protected String getPKfieldName() {
        return "id";
    }
    
    

    @Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		final Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof SynchConfigSearchBean) {
			final SynchConfigSearchBean sb = (SynchConfigSearchBean)searchBean;
			if(sb.isExcludeBooleanProperties()) {
				 final Example example = Example.create(new SynchConfigEntity());
				 example.excludeProperty("usePolicyMap"); // exclude boolean properties
				 example.excludeProperty("useTransformationScript");
				 example.excludeProperty("policyMapBeforeTransformation");
				 example.excludeProperty("useSystemPath");
				 example.excludeProperty("searchScope");
				 criteria.add(example);
			}
		}
		return criteria;
	}
}
