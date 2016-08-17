package org.openiam.srvc.idm;

import org.openiam.base.request.SynchReviewRequest;
import org.openiam.base.response.SynchReviewResponse;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.SynchReviewSearchBean;
import org.openiam.idm.srvc.synch.dto.SynchReview;
import org.openiam.idm.srvc.synch.dto.SynchReviewRecord;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(targetNamespace = "http://www.openiam.org/service/synchReview", name = "SynchReviewWebService")
public interface SynchReviewWebService {

    @WebMethod
    List<SynchReview> findBeans(@WebParam(name = "searchBean", targetNamespace = "") SynchReviewSearchBean searchBean,
                         @WebParam(name = "from", targetNamespace = "") int from,
                         @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    Integer countBeans(@WebParam(name = "searchBean", targetNamespace = "") SynchReviewSearchBean searchBean);

    @WebMethod
    Response deleteByIds(@WebParam(name = "deleteIds", targetNamespace = "") List<String> deleteIds);

    @WebMethod
    Response delete(@WebParam(name = "synchReviewId", targetNamespace = "") String synchReviewId);

    @WebMethod
    SynchReviewRecord getHeaderReviewRecord(@WebParam(name = "synchReviewId", targetNamespace = "") String synchReviewId);

    @WebMethod
    List<SynchReviewRecord> getRecordsBySynchReviewId(@WebParam(name = "synchReviewId", targetNamespace = "") String synchReviewId,
                                                      @WebParam(name = "from", targetNamespace = "") int from,
                                                      @WebParam(name = "size", targetNamespace = "") int size);
    @WebMethod
    Integer getRecordsCountBySynchReviewId(@WebParam(name = "synchReviewId", targetNamespace = "") String synchReviewId);

    @WebMethod
    SynchReviewResponse updateSynchReview(@WebParam(name = "synchReviewRequest", targetNamespace = "") SynchReviewRequest synchReviewRequest);

    @WebMethod
    SynchReviewResponse executeSynchReview(@WebParam(name = "synchReviewRequest", targetNamespace = "") SynchReviewRequest synchReviewRequest);

}
