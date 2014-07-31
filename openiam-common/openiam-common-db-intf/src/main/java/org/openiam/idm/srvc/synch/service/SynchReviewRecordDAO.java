package org.openiam.idm.srvc.synch.service;


import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.synch.domain.SynchReviewRecordEntity;

import java.util.List;

public interface SynchReviewRecordDAO extends BaseDao<SynchReviewRecordEntity, String> {

    SynchReviewRecordEntity getHeaderReviewRecord(String synchReviewId);

    List<SynchReviewRecordEntity> getRecordsBySynchReviewId(String synchReviewId, int from, int size);

    int getRecordsCountBySynchReviewId(String synchReviewId);

}
