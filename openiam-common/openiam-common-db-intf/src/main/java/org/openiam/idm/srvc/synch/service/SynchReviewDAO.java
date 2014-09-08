package org.openiam.idm.srvc.synch.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.synch.domain.SynchReviewEntity;
import org.openiam.idm.srvc.synch.domain.SynchReviewRecordEntity;

import java.util.List;


public interface SynchReviewDAO extends BaseDao<SynchReviewEntity, String> {

    List<SynchReviewEntity> findAllBySynchConfigId(String configId);

}
