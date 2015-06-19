package org.openiam.idm.srvc.synch.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.synch.domain.SynchConfigDataMappingEntity;
import org.springframework.stereotype.Repository;

@Repository("synchConfigDataMappingDAO")
public class SynchConfigDataMappingDAOImpl extends BaseDaoImpl<SynchConfigDataMappingEntity, String> implements SynchConfigDataMappingDAO {

	private static final Log log = LogFactory
			.getLog(SynchConfigDataMappingDAOImpl.class);

	public SynchConfigDataMappingEntity add(SynchConfigDataMappingEntity transientInstance) {
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

	public void remove(SynchConfigDataMappingEntity persistentInstance) {
		log.debug("deleting SynchConfigDataMapping instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

    @Override
	public SynchConfigDataMappingEntity merge(SynchConfigDataMappingEntity detachedInstance) {
		log.debug("merging SynchConfigDataMapping instance");
		try {
            SynchConfigDataMappingEntity ret = (SynchConfigDataMappingEntity) getSession().merge(detachedInstance);
			log.debug("merge successful");
            return ret;

		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public SynchConfigDataMappingEntity findById(java.lang.String id) {
		log.debug("getting SynchConfigDataMapping instance with id: " + id);
		try {
            SynchConfigDataMappingEntity instance = (SynchConfigDataMappingEntity) getSession()
                    .get(SynchConfigDataMappingEntity.class, id);
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

    @Override
    protected String getPKfieldName() {
        return "id";
    }
}
