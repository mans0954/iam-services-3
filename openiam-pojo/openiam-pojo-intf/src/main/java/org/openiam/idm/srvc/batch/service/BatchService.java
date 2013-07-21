package org.openiam.idm.srvc.batch.service;

import java.util.List;

import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;

public interface BatchService {

	public int count(final BatchTaskEntity entity);
	public List<BatchTaskEntity> findBeans(final BatchTaskEntity entity, final int from, final int size);
	
	public void save(final BatchTaskEntity entity);
	
	public void delete(final String id);
	
	public BatchTaskEntity findById(final String id);
}
