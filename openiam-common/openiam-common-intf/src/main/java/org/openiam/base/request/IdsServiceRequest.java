package org.openiam.base.request;

import java.util.Collection;
import java.util.List;

/**
 * @author zaporozhec
 */
public class IdsServiceRequest extends BaseServiceRequest {
    private List<String> ids;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IdsServiceRequest{");
        sb.append("ids=").append(ids);
        sb.append('}');
        return sb.toString();
    }
}
