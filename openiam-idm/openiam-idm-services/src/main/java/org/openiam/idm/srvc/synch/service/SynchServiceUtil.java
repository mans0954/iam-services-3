package org.openiam.idm.srvc.synch.service;

import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.script.ScriptIntegration;

import java.util.Map;

public class SynchServiceUtil {

    public static String setUserDataFromPolicy(Policy policy,
            Map<String, Object> bindingMap, ScriptIntegration se) throws ScriptEngineException {
        if (policy != null) {
            String url = policy.getRuleSrcUrl();
            if (url != null) {
                return (String)se.execute(bindingMap, url);
            }
        }
        return null;
    }
}
