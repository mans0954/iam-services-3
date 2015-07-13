package org.openiam.xacml.srvc.rest.service;

import org.openiam.xacml.srvc.rest.request.AbstractRestRequest;
import org.openiam.xacml.srvc.rest.response.AbstractRestResponse;

import java.util.List;

/**
 * Created by zaporozhec on 7/10/15.
 */
public interface AbstractRestService<Request extends AbstractRestRequest, Response extends AbstractRestResponse> {

    public Response add(Request policyEntity) throws Exception;

    public Response update(Request policyEntity) throws Exception;

    public Response findById(String id) throws Exception;

    public Response delete(String id) throws Exception;

}
