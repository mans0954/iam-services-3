package org.openiam.xacml.srvc.rest.request;

import org.openiam.base.KeyDTO;
import org.openiam.xacml.srvc.rest.request.constant.RestRequestType;

/**
 * Created by zaporozhec on 7/14/15.
 */
public abstract class AbstractDTORestRequest<DTO extends KeyDTO> extends AbstractRestRequest {

    private DTO object;

    public AbstractDTORestRequest() {
        super(RestRequestType.CRUD);
    }

    public DTO getObject() {
        return object;
    }

    public void setObject(DTO object) {
        this.object = object;
    }
}
