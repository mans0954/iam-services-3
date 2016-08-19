package org.openiam.srvc.idm;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.dozer.converter.SynchReviewDozerConverter;
import org.openiam.idm.searchbeans.SynchReviewSearchBean;
import org.openiam.idm.srvc.synch.dto.SynchReview;
import org.openiam.idm.srvc.synch.dto.SynchReviewRecord;
import org.openiam.base.request.SynchReviewRequest;
import org.openiam.base.response.SynchReviewResponse;
import org.openiam.idm.srvc.synch.service.SynchReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import java.util.List;

@WebService(endpointInterface = "org.openiam.srvc.idm.SynchReviewWebService",
        targetNamespace = "http://www.openiam.org/service/synchReview",
        portName = "SynchReviewWebServicePort",
        serviceName = "SynchReviewWebService")
@Component("synchReviewWebService")
public class SynchReviewWebServiceImpl implements SynchReviewWebService {

    protected static final Log log = LogFactory.getLog(SynchReviewWebServiceImpl.class);

    @Autowired
    protected SynchReviewService synchReviewService;
    @Autowired
    SynchReviewDozerConverter synchReviewDozerConverter;

    @Override
    public List<SynchReview> findBeans(SynchReviewSearchBean searchBean, int from, int size) {
        return synchReviewService.findBeans(searchBean, from, size);
    }
    @Override
    public Integer countBeans(SynchReviewSearchBean searchBean) {
        return synchReviewService.countBeans(searchBean);
    }

    @Override
    public Response deleteByIds(List<String> deleteIds) {
        return synchReviewService.deleteByIds(deleteIds);
    }

    @Override
    public Response delete(String synchReviewId) {
        return synchReviewService.delete(synchReviewId);
    }

    @Override
    public SynchReviewRecord getHeaderReviewRecord(String synchReviewId) {
        return synchReviewService.getHeaderReviewRecord(synchReviewId);
    }

    @Override
    public List<SynchReviewRecord> getRecordsBySynchReviewId(String synchReviewId, int from, int size) {
        return synchReviewService.getRecordsBySynchReviewId(synchReviewId, from, size);
    }

    @Override
    public Integer getRecordsCountBySynchReviewId(String synchReviewId) {
        return synchReviewService.getRecordsCountBySynchReviewId(synchReviewId);
    }

    @Override
    public SynchReviewResponse updateSynchReview(SynchReviewRequest synchReviewRequest) {
        return synchReviewService.updateSynchReview(synchReviewRequest);
    }

    @Override
    public SynchReviewResponse executeSynchReview(SynchReviewRequest synchReviewRequest) {
        return synchReviewService.executeSynchReview(synchReviewRequest);
    }
}
