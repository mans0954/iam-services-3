package org.openiam.idm.srvc.pswd.dto;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.pswd.rule.PasswordRuleException;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PasswordValidationResponse", propOrder = {
        "responseValues",
        "minBound",
        "maxBound",
        "rules"
})
public class PasswordValidationResponse extends Response {
    public PasswordValidationResponse() {}
    public PasswordValidationResponse(final ResponseStatus status) {
        super(status);
    }
    private List<PasswordRule> rules;
    private Object minBound;
    private Object maxBound;
    private List<Object> responseValues;
    public void addResponseValue(final Object obj) {
        if(obj != null) {
            if(responseValues == null) {
                responseValues = new LinkedList<Object>();
            }
        }
    }
    public List<Object> getResponseValues() {
        return responseValues;
    }
    public void setResponseValues(List<Object> responseValues) {
        this.responseValues = responseValues;
    }
    public Object[] getResponseValueAsArray() {
        Object[] retVal = null;
        if(responseValues != null) {
            retVal = new Object[responseValues.size()];
            for(int i = 0; i < responseValues.size(); i++) {
                retVal[i] = responseValues.get(i);
            }
        }
        return retVal;
    }
    public Object getMinBound() {
        return minBound;
    }
    public void setMinBound(Object minBound) {
        this.minBound = minBound;
    }
    public Object getMaxBound() {
        return maxBound;
    }
    public void setMaxBound(Object maxBound) {
        this.maxBound = maxBound;
    }
    public boolean hasMinBound() {
        return (minBound != null);
    }
    public boolean hasMaxBound() {
        return (maxBound != null);
    }
    public List<PasswordRule> getRules() {
        return rules;
    }
    public void setRules(List<PasswordRule> rules) {
        this.rules = rules;
    }
}