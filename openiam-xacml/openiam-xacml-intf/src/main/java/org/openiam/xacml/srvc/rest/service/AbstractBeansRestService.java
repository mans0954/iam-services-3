package org.openiam.xacml.srvc.rest.service;

import org.openiam.xacml.srvc.rest.request.AbstractRestRequest;
import org.openiam.xacml.srvc.rest.request.AbstractSearchRestRequest;
import org.openiam.xacml.srvc.rest.response.AbstractRestResponse;

import java.util.List;

/**
 * Created by zaporozhec on 7/10/15.
 */
public interface AbstractBeansRestService<SearchRequest extends AbstractSearchRestRequest, Request extends AbstractRestRequest, Response extends AbstractRestResponse> extends AbstractRestService<Request, Response> {

    public Response findBeans(SearchRequest searchRequest) throws Exception;

}
