package org.openiam.xacml.srvc.rest.request;

import org.openiam.xacml.srvc.rest.request.constant.RestRequestType;

/**
 * Created by zaporozhec on 7/14/15.
 */
public abstract class AbstractRestRequest {

    private RestRequestType type;

    public AbstractRestRequest(RestRequestType type) {
        this.type = type;
    }

    public RestRequestType getType() {
        return type;
    }
}
