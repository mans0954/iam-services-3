package org.openiam.base.response;

import org.openiam.base.TreeObjectId;
import org.openiam.base.ws.Response;

import java.util.List;

/**
 * Created by alexander on 09/08/16.
 */
public class TreeObjectIdListServiceResponse extends Response {
    private List<TreeObjectId> treeObjectIds;

    public List<TreeObjectId> getTreeObjectIds() {
        return treeObjectIds;
    }

    public void setTreeObjectIds(List<TreeObjectId> treeObjectIds) {
        this.treeObjectIds = treeObjectIds;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TreeObjectIdListServiceResponse{");
        sb.append("treeObjectIds=").append(treeObjectIds);
        sb.append('}');
        return sb.toString();
    }
}
