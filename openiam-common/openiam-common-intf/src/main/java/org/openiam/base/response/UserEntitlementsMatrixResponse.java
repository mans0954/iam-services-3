package org.openiam.base.response;

import org.openiam.base.ws.Response;
import org.openiam.model.UserEntitlementsMatrix;

/**
 * Created by alexander on 12/09/16.
 */
public class UserEntitlementsMatrixResponse extends Response {
    private UserEntitlementsMatrix matrix;

    public UserEntitlementsMatrix getMatrix() {
        return matrix;
    }

    public void setMatrix(UserEntitlementsMatrix matrix) {
        this.matrix = matrix;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserEntitlementsMatrixResponse{");
        sb.append(super.toString());
        sb.append(", matrix=").append(matrix);
        sb.append('}');
        return sb.toString();
    }
}
