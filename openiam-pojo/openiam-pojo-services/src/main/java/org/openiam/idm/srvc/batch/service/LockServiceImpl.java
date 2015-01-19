package org.openiam.idm.srvc.batch.service;

import javax.transaction.Transactional;

import org.openiam.exception.LockObtainException;
import org.openiam.exception.UnlockException;
import org.openiam.idm.srvc.batch.dao.LockTableDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LockServiceImpl implements LockService {

    @Autowired
    private LockTableDAO lockDAO;
    
    @Transactional
	public void lock(final String name) throws LockObtainException {
		lockDAO.lock(name);
	}
	
	@Transactional
	public void unlock(final String name) throws UnlockException {
		lockDAO.unlock(name);
	}
}
