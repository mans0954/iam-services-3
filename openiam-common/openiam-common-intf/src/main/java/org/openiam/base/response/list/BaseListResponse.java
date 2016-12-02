package org.openiam.base.response.list;

import org.openiam.base.ws.Response;

import java.util.List;

/**
 * Created by alexander on 22/09/16.
 */
public class BaseListResponse<T> extends Response {
    private List<T> list;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
        sb.append("{");
        sb.append(super.toString());
        sb.append(", list=").append(list);
        sb.append('}');
        return sb.toString();
    }
}
