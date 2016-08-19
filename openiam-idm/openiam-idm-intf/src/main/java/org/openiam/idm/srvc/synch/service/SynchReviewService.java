package org.openiam.idm.srvc.synch.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.SynchReviewSearchBean;
import org.openiam.idm.srvc.synch.dto.SynchReview;
import org.openiam.idm.srvc.synch.dto.SynchReviewRecord;
import org.openiam.base.request.SynchReviewRequest;
import org.openiam.base.response.SynchReviewResponse;

import java.util.List;

public interface SynchReviewService {

    List<SynchReview> findBeans(SynchReviewSearchBean searchBean, int from, int size);

    Integer countBeans(SynchReviewSearchBean searchBean);

    Response deleteByIds(List<String> deleteIds);

    Response delete(String synchReviewId);

    SynchReviewRecord getHeaderReviewRecord(String synchReviewId);

    List<SynchReviewRecord> getRecordsBySynchReviewId(String synchReviewId, int from, int size);

    Integer getRecordsCountBySynchReviewId(String synchReviewId);

    SynchReviewResponse updateSynchReview(SynchReviewRequest synchReviewRequest);

    SynchReviewResponse executeSynchReview(SynchReviewRequest synchReviewRequest);

}
