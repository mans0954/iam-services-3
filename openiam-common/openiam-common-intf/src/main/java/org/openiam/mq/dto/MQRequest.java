package org.openiam.mq.dto;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.util.OpenIAMUtils;

/**
 * Created by alexander on 06/07/16.
 */
public class MQRequest<RequestBody extends BaseServiceRequest, API extends OpenIAMAPI>  extends AbstractMQMessage{

    private String replyTo;
    private API requestApi;
    protected RequestBody requestBody;

    public MQRequest(){}
    public MQRequest(API requestApi, RequestBody requestBody){
        this.requestApi=requestApi;
        this.requestBody=requestBody;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public API getRequestApi() {
        return requestApi;
    }

    public void setRequestApi(API requestApi) {
        this.requestApi = requestApi;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MQRequest{");
//        sb.append("correlationID='").append((getCorrelationId()!=null)?OpenIAMUtils.byteArrayToString(getCorrelationId()):"null").append('\'');
        sb.append(", replyTo='").append(replyTo).append('\'');
        sb.append(", requestApi=").append(requestApi);
        sb.append(", requestBody=").append(requestBody);
        sb.append('}');
        return sb.toString();
    }
}
