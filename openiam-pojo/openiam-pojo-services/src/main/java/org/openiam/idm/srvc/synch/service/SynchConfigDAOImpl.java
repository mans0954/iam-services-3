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
import org.openiam.idm.srvc.synch.domain.SynchConfigEntity;
import org.springframework.stereotype.Repository;

/**
 * Home object for domain model class SynchConfig.
 */
@Repository("synchConfigDAO")
public class SynchConfigDAOImpl extends BaseDaoImpl<SynchConfigEntity, String> implements SynchConfigDAO {

	private static final Log log = LogFactory.getLog(SynchConfigDAOImpl.class);

	public SynchConfigEntity add(SynchConfigEntity transientInstance) {
		log.debug("persisting SynchConfig instance");
		try {
            getSession().persist(transientInstance);
			log.debug("persist successful");
			return transientInstance;
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(SynchConfigEntity persistentInstance) {
		log.debug("deleting SynchConfig instance");
		try {
            getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public SynchConfigEntity merge(SynchConfigEntity detachedInstance) {
		log.debug("merging SynchConfig instance");
		try {
            SynchConfigEntity ret = (SynchConfigEntity) getSession().merge(detachedInstance);
			log.debug("merge successful");
            return ret;

		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public SynchConfigEntity findById(java.lang.String id) {
		log.debug("getting SynchConfig instance with id: " + id);

		try {
            SynchConfigEntity instance = (SynchConfigEntity)getCriteria()
                    .add(Restrictions.idEq(id)).uniqueResult();
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
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
		log.debug("getting all synchronization configurations  "  );
        Criteria criteria = getCriteria().addOrder(Order.asc("name"));
        return (List<SynchConfigEntity>)criteria.list();
	}

	public int updateExecTime(String configId, Timestamp execTime) {
		log.debug("Updates the last execution   "  );

		Query qry = getSession().createQuery("UPDATE org.openiam.idm.srvc.synch.domain.SynchConfigEntity sc " +
                " 					SET  sc.lastExecTime = :execTime  " +
                " 					WHERE  sc.synchConfigId = :configId	");
		
		qry.setTimestamp("execTime", execTime);
		qry.setString("configId", configId);
		
		return qry.executeUpdate();
	}

    public int updateLastRecProcessed(String configId,String processTime) {
		log.debug("Updates the last execution   "  );

		Query qry = getSession().createQuery(" UPDATE org.openiam.idm.srvc.synch.domain.SynchConfigEntity sc " +
                " 					SET  sc.lastRecProcessed = :processTime  " +
                " 					WHERE  sc.synchConfigId = :configId	");

		qry.setString("processTime",processTime);
		qry.setString("configId", configId);

		return qry.executeUpdate();
	}

    @Override
    protected String getPKfieldName() {
        return "synchConfigId";
    }

    @Override
    protected Criteria getExampleCriteria(SynchConfigEntity config) {
        Example example = Example.create(config);
        example.excludeProperty("usePolicyMap"); // exclude boolean properties
        example.excludeProperty("useTransformationScript");
        example.excludeProperty("policyMapBeforeTransformation");
        example.excludeProperty("useSystemPath");
        return getCriteria().add(example);
    }

}
