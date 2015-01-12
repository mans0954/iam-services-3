package org.openiam.idm.srvc.batch.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.store.LockObtainFailedException;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.LockAcquisitionException;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.exception.LockObtainException;
import org.openiam.exception.UnlockException;
import org.openiam.idm.srvc.batch.dao.BatchConfigDAO;
import org.openiam.idm.srvc.batch.dao.LockTableDAO;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.domain.LockTableEntity;
import org.springframework.stereotype.Repository;

@Repository
public class LockTableDAOImpl extends BaseDaoImpl<LockTableEntity, String> implements LockTableDAO {

	@Override
	protected String getPKfieldName() {
		return "id";
	}

	@Override
	public void lock(String name) throws LockObtainException {
		final LockTableEntity entity = new LockTableEntity();
		entity.setName(name);
		try {
			getSession().save(entity);
		} catch(Throwable e) {
			log.warn("Can't get lock", e);
			throw new LockObtainException(String.format("Can't obtain lock %s.  This  is likely because another node is currently using it", name), e);
		}
	}

	@Override
	public void unlock(String name) throws UnlockException {
		final LockTableEntity example = new LockTableEntity();
		example.setName(name);
		
		final List<LockTableEntity> list = getByExample(example);
		
		if(CollectionUtils.isEmpty(list)) {
			throw new UnlockException(String.format("Can't unlock %s.  This is a fatal exception, as the current thread SHOULD have been the one to lock it in the first place", name));
		} else {
			delete(list.get(0));
		}
	}

	@Override
	protected Criteria getExampleCriteria(LockTableEntity t) {
		final Criteria criteria = super.getCriteria();
		if(t != null) {
			if(StringUtils.isNotBlank(t.getName())) {
				criteria.add(Restrictions.eqOrIsNull("name", t.getName()));
			}
		}
		return criteria;
	}
	
	
}
