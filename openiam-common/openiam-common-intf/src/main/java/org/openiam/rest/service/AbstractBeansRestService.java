package org.openiam.rest.service;

import org.openiam.rest.request.AbstractRestRequest;
import org.openiam.rest.request.AbstractSearchRestRequest;
import org.openiam.rest.response.AbstractRestResponse;

/**
 * Created by zaporozhec on 7/10/15.
 */
public abstract class AbstractBeansRestService<SearchRequest extends AbstractSearchRestRequest, Request extends AbstractRestRequest, Response extends AbstractRestResponse> extends AbstractRestService<Request, Response> {

    public abstract Response findBeans(SearchRequest searchRequest) throws Exception;

}
