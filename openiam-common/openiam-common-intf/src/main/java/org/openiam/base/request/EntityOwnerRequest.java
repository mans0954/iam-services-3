package org.openiam.base.request;

import java.util.Date;
import java.util.Set;

/**
 * Created by alexander on 12/09/16.
 */
public class EntityOwnerRequest extends BaseServiceRequest {
    private Set<String> entityIdSet;
    private Date date;

    public Set<String> getEntityIdSet() {
        return entityIdSet;
    }

    public void setEntityIdSet(Set<String> entityIdSet) {
        this.entityIdSet = entityIdSet;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("EntityOwnerRequest{");
        sb.append(super.toString());
        sb.append(", entityIdSet=").append(entityIdSet);
        sb.append(", date=").append(date);
        sb.append('}');
        return sb.toString();
    }
}
