package org.openiam.idm.srvc.batch.service;

import org.openiam.exception.LockObtainException;
import org.openiam.exception.UnlockException;

public interface LockService {

	public void lock(final String name) throws LockObtainException;
	public void unlock(final String name) throws UnlockException;
}
