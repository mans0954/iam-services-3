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
		try {
            getSession().persist(transientInstance);
			return transientInstance;
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(SynchConfigDataMappingEntity persistentInstance) {
		try {
			getSession().delete(persistentInstance);
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

    @Override
	public SynchConfigDataMappingEntity merge(SynchConfigDataMappingEntity detachedInstance) {
		try {
            SynchConfigDataMappingEntity ret = (SynchConfigDataMappingEntity) getSession().merge(detachedInstance);
            return ret;

		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public SynchConfigDataMappingEntity findById(java.lang.String id) {
		try {
            SynchConfigDataMappingEntity instance = (SynchConfigDataMappingEntity) getSession()
                    .get(SynchConfigDataMappingEntity.class, id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

    @Override
    protected String getPKfieldName() {
        return "mappingId";
    }
}
