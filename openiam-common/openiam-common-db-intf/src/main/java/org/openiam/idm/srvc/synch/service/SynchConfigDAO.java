package org.openiam.idm.srvc.synch.service;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Example;
import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.synch.domain.SynchConfigEntity;

public interface SynchConfigDAO extends BaseDao<SynchConfigEntity, String> {

    SynchConfigEntity findById(java.lang.String id) ;

    SynchConfigEntity add(SynchConfigEntity instance);

    SynchConfigEntity merge(SynchConfigEntity instance);

	void remove(SynchConfigEntity instance);
	
	List<SynchConfigEntity> findAllConfig();
	
	int updateExecTime(String configId, Timestamp execTime);

    int updateLastRecProcessed(String configId,String processTime);

}
