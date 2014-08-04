package org.openiam.provision.service;

import org.apache.commons.lang.StringUtils;
import org.openiam.provision.dto.ProvisionActionEnum;
import org.openiam.provision.dto.ProvisionActionEvent;

import java.util.HashMap;

public class ActionEventBuilder {

    ProvisionActionEvent event;

    public ActionEventBuilder() {
        event = new ProvisionActionEvent();
    }

    public ActionEventBuilder(String targetUserId, String requesterId, ProvisionActionEnum action) {
        event = new ProvisionActionEvent();
        event.setTargetUserId(targetUserId);
        event.setRequesterId(requesterId);
        event.setAction(action);
    }

    public ProvisionActionEvent build() {
        return event;
    }

    public ActionEventBuilder setTargetUserId(String targetUserId) {
        event.setTargetUserId(targetUserId);
        return this;
    }

    public ActionEventBuilder setRequesterId(String requesterId) {
        event.setRequesterId(requesterId);
        return this;
    }

    public ActionEventBuilder setActionType(ProvisionActionEnum action) {
        event.setAction(action);
        return this;
    }

    public ActionEventBuilder addParam(String name, Object value) {
        if (event.getParams() == null) {
            event.setParams(new HashMap<String, Object>());
        }
        if (StringUtils.isNotBlank(name)) {
            event.getParams().put(name, value);
        }
        return this;
    }

}
