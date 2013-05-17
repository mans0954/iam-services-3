package org.openiam.idm.srvc.synch.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.synch.domain.SynchConfigDataMappingEntity;

public interface SynchConfigDataMappingDAO extends BaseDao<SynchConfigDataMappingEntity, String> {

    SynchConfigDataMappingEntity findById(java.lang.String id) ;

    SynchConfigDataMappingEntity add(SynchConfigDataMappingEntity instance);

    SynchConfigDataMappingEntity merge(SynchConfigDataMappingEntity instance);

	void remove(SynchConfigDataMappingEntity instance);
	
	//List<SynchConfigDataMapping> findDataMappingByConfigId(String configId);
	
}
