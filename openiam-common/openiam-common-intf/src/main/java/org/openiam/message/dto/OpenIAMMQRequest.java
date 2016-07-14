package org.openiam.message.dto;

import org.openiam.message.constants.OpenIAMAPI;

/**
 * Created by alexander on 06/07/16.
 */
public class OpenIAMMQRequest<RequestBody>  extends AbstractMQMessage{

    private String replyTo;
    private OpenIAMAPI requestApi;
    protected RequestBody requestBody;
    private String languageId;



    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public OpenIAMAPI getRequestApi() {
        return requestApi;
    }

    public void setRequestApi(OpenIAMAPI requestApi) {
        this.requestApi = requestApi;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MQRequest{");
        sb.append("correlationID='").append(getCorrelationID()).append('\'');
        sb.append(", replyTo='").append(replyTo).append('\'');
        sb.append(", requestApi=").append(requestApi);
        sb.append(", requestBody=").append(requestBody);
        sb.append(", languageId=").append(languageId);
        sb.append('}');
        return sb.toString();
    }
}
