package org.openiam.base.request;

import org.openiam.base.KeyDTO;

/**
 * Created by alexander on 09/08/16.
 */
public class BaseGrudServiceRequest<V extends KeyDTO> extends BaseServiceRequest {
    private V object;

    public BaseGrudServiceRequest(V object) {
        this.object = object;
    }

    public V getObject() {
        return object;
    }

    public void setObject(V object) {
        this.object = object;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BaseGrudServiceRequest{");
        sb.append("object=").append(object);
        sb.append('}');
        return sb.toString();
    }
}
