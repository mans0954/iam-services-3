package org.openiam.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.openiam.rest.request.constant.RestRequestType;

/**
 * Created by zaporozhec on 7/14/15.
 */
@JsonIgnoreProperties({"type"})
public abstract class AbstractRestRequest {

    private RestRequestType type;

    public AbstractRestRequest(RestRequestType type) {
        this.type = type;
    }

    public RestRequestType getType() {
        return type;
    }
}
