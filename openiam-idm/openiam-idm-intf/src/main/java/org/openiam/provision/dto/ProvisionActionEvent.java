package org.openiam.provision.dto;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.HashMap;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvisionActionEvent", propOrder = {
        "targetUserId",
        "requesterId",
        "action",
        "params"
})
public class ProvisionActionEvent {

    private String targetUserId;

    private String requesterId;

    private ProvisionActionEnum action;

    private Map<String, Object> params = new HashMap<String, Object>();


    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public ProvisionActionEnum getAction() {
        return action;
    }

    public void setAction(ProvisionActionEnum action) {
        this.action = action;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
