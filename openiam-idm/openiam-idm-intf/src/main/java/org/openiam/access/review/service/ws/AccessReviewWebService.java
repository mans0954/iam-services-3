package org.openiam.access.review.service.ws;

import org.openiam.access.review.model.AccessViewFilterBean;
import org.openiam.access.review.model.AccessViewResponse;
import org.openiam.idm.srvc.lang.dto.Language;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Created by alexander on 24.11.14.
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/access/review/service", name = "AccessReviewWebService")
public interface AccessReviewWebService {
    @WebMethod
    AccessViewResponse getAccessReviewTree(final @WebParam(name = "filter", targetNamespace = "") AccessViewFilterBean filter,
                                           final @WebParam(name = "viewType", targetNamespace = "") String viewType,
                                           final @WebParam(name = "language", targetNamespace = "") Language language);
    @WebMethod
    AccessViewResponse getAccessReviewSubTree(final @WebParam(name = "parentId", targetNamespace = "") String parentId,
                                              final @WebParam(name = "parentBeanType", targetNamespace = "") String parentBeanType,
                                              final @WebParam(name = "filter", targetNamespace = "") AccessViewFilterBean filter,
                                              final @WebParam(name = "viewType", targetNamespace = "") String viewType,
                                              final @WebParam(name = "language", targetNamespace = "") Language language);
}
