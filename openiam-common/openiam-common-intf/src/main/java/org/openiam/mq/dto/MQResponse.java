package org.openiam.mq.dto;

import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.EsbErrorToken;
import org.openiam.util.OpenIAMUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexander on 06/07/16.
 */
public class MQResponse<ResponseBody> extends AbstractMQMessage {
    protected ResponseStatus status = ResponseStatus.SUCCESS;
    protected ResponseCode errorCode;
    protected String errorText;
    protected Map<String, String> fieldMappings;
    private List<EsbErrorToken> errorTokenList;
    private String stacktraceText;

    private ResponseBody responseBody;
    public ResponseStatus getStatus() {
        return status;
    }

    public MQResponse<ResponseBody> succeed() {
        this.status = ResponseStatus.SUCCESS;
        return this;
    }

    public MQResponse<ResponseBody> fail() {
        this.status = ResponseStatus.FAILURE;
        return this;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public ResponseCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ResponseCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public boolean isSuccess() {
        return ResponseStatus.SUCCESS.equals(status);
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    public List<EsbErrorToken> getErrorTokenList() {
        return errorTokenList;
    }

    public void setErrorTokenList(List<EsbErrorToken> errorTokenList) {
        this.errorTokenList = errorTokenList;
    }

    public void addErrorToken(EsbErrorToken errorToken){
        if(errorTokenList==null)
            errorTokenList = new ArrayList<EsbErrorToken>();
        errorTokenList.add(errorToken);
    }

    public String getFieldMapping(final String field) {
        return (field != null && fieldMappings != null) ? fieldMappings.get(field) : null;
    }

    public void addFieldMapping(final String field, final String value) {
        if(field != null && value != null) {
            if(this.fieldMappings == null) {
                this.fieldMappings = new HashMap<>();
            }
            this.fieldMappings.put(field, value);
        }
    }

    public Map<String, String> getFieldMappings() {
        return fieldMappings;
    }

    public void setFieldMappings(Map<String, String> fieldMappings) {
        this.fieldMappings = fieldMappings;
    }



    public String getStacktraceText() {
        return stacktraceText;
    }

    public void setStacktraceText(String stacktraceText) {
        this.stacktraceText = stacktraceText;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MQResponse{");
        sb.append("correlationId=").append(OpenIAMUtils.byteArrayToString(getCorrelationId()));
        sb.append(", status=").append(status);
        sb.append(", errorCode=").append(errorCode);
        sb.append(", errorText='").append(errorText).append('\'');
        sb.append(", fieldMappings=").append(fieldMappings);
        sb.append(", errorTokenList=").append(errorTokenList);
        sb.append(", stacktraceText='").append(stacktraceText).append('\'');
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
