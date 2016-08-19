package org.openiam.base.response;

import org.openiam.base.ws.Response;

/**
 * Created by alexander on 09/08/16.
 */
public class CountResponse extends Response {
    private int rowCount = 0;

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CountResponse{");
        sb.append(super.toString());
        sb.append(", rowCount=").append(rowCount);
        sb.append('}');
        return sb.toString();
    }
}
