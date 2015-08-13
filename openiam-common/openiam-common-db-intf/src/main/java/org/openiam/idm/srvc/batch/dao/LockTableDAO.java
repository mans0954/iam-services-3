package org.openiam.idm.srvc.batch.dao;

import org.openiam.core.dao.BaseDao;
import org.openiam.exception.LockObtainException;
import org.openiam.exception.UnlockException;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.domain.LockTableEntity;

public interface LockTableDAO extends BaseDao<LockTableEntity, String>{

	void lock(final String id) throws LockObtainException;
	void unlock(final String id) throws UnlockException;
}
