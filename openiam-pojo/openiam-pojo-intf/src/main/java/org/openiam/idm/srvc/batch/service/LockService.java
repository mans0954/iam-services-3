package org.openiam.idm.srvc.batch.service;

import org.openiam.exception.LockObtainException;
import org.openiam.exception.UnlockException;

public interface LockService {

	void lock(final String name) throws LockObtainException;
	void unlock(final String name) throws UnlockException;
}
