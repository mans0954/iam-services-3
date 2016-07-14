package org.openiam.message.dto;

import org.openiam.base.ws.Response;

/**
 * Created by alexander on 06/07/16.
 */
public class OpenIAMMQResponse<ResponseBody extends Response> extends AbstractMQMessage {
    private ResponseBody responseBody;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("OpenIAMMQResponse{");
        sb.append("correlationID='").append(getCorrelationID()).append('\'');
        sb.append(", responseBody=").append(responseBody);
        sb.append('}');
        return sb.toString();
    }



    public ResponseBody getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
    }
}
