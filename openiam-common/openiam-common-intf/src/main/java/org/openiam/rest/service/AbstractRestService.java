package org.openiam.rest.service;

import org.openiam.rest.request.AbstractRestRequest;
import org.openiam.rest.response.AbstractRestResponse;

/**
 * Created by zaporozhec on 7/10/15.
 */
public abstract class AbstractRestService<Request extends AbstractRestRequest, Response extends AbstractRestResponse> {

    public abstract Response add(Request policyEntity) throws Exception;

    public abstract Response update(Request policyEntity) throws Exception;

    public abstract Response findById(String id, boolean deepCopy) throws Exception;

    public abstract Response delete(String id) throws Exception;

}
