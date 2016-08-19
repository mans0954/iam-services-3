package org.openiam.base.response;

import org.openiam.base.ws.Response;

/**
 * Created by alexander on 09/08/16.
 */
public class IdServiceResponse extends Response {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("IdServiceResponse{");
        sb.append(super.toString());
        sb.append(", id=").append(id);
        sb.append('}');
        return sb.toString();
    }

    public Response convertToBase(){
        Response response = new Response();
        response.setResponseValue(this.id);
        response.setErrorCode(this.errorCode);
        response.setErrorText(this.errorText);
        response.setErrorTokenList(this.getErrorTokenList());
        response.setFieldMappings(this.getFieldMappings());
        response.setStacktraceText(this.getStacktraceText());
        response.setStatus(this.getStatus());

        return response;
    }
}
