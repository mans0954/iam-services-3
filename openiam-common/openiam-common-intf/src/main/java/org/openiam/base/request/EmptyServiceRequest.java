package org.openiam.base.request;

/**
 * Created by alexander on 15/11/16.
 */
public final class EmptyServiceRequest extends BaseServiceRequest {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("EmptyServiceRequest{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
