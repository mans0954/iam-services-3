package org.openiam.idm.srvc.batch.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.batch.dao.BatchConfigDAO;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BatchServiceImpl implements BatchService {

    @Autowired
    private BatchConfigDAO batchDao;

    @Override
    public List<BatchTaskEntity> findBeans(BatchTaskEntity entity, int from,
            int size) {
        return batchDao.getByExample(entity, from, size);
    }

    @Override
    public int count(BatchTaskEntity entity) {
        return batchDao.count(entity);
    }

    @Override
    public void save(BatchTaskEntity entity) {
        if (entity != null) {
            if (StringUtils.isBlank(entity.getId())) {
                batchDao.save(entity);
            } else {
            	final BatchTaskEntity dbEntity = batchDao.findById(entity.getId());
            	if(dbEntity != null) {
            		entity.setLastExecTime(dbEntity.getLastExecTime());
            		batchDao.merge(entity);
            	}
            }
        }
    }

    @Override
    public void delete(String id) {
        if (StringUtils.isNotBlank(id)) {
            final BatchTaskEntity entity = batchDao.findById(id);
            if (entity != null) {
                batchDao.delete(entity);
            }
        }
    }

    @Override
    public BatchTaskEntity findById(String id) {
        return batchDao.findById(id);
    }
}
