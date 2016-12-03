package org.openiam.base.response.list;


import org.openiam.base.response.list.BaseListResponse;

/**
 * Created by alexander on 22/09/16.
 */
public final class ClassListResponse extends BaseListResponse<Class<?>> {

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ClassListResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
