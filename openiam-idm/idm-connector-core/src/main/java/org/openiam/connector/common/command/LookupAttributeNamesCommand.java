package org.openiam.connector.common.command;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.LookupRequest;
import org.openiam.base.response.LookupAttributeResponse;
import org.openiam.idm.srvc.mngsys.service.AttributeNamesLookupService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("lookupAttributeNamesCommand")
public class LookupAttributeNamesCommand<ExtObject extends ExtensibleObject> extends AbstractCommand<LookupRequest<ExtObject>, LookupAttributeResponse> {

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;

    @Override
    public LookupAttributeResponse execute(LookupRequest<ExtObject> lookupRequest) throws ConnectorDataException {

        LookupAttributeResponse respType = new LookupAttributeResponse();

        Object attrNames = null;
        if (StringUtils.isNotBlank(lookupRequest.getScriptHandler())) {
            try {

                Map<String, Object> binding = new HashMap<String, Object>();
                Map<String, Object> bindingMap = new HashMap<String, Object>();
                bindingMap.put("binding", binding);

                AttributeNamesLookupService lookupScript =
                        (AttributeNamesLookupService) scriptRunner.instantiateClass(bindingMap,
                                lookupRequest.getScriptHandler());
                switch (lookupRequest.getExecutionMode()) {
                    case "POLICY_MAP":
                        attrNames = lookupScript.lookupPolicyMapAttributes(bindingMap);
                        break;
                    case "MANAGED_SYSTEM":
                        attrNames = lookupScript.lookupManagedSystemAttributes(bindingMap);
                        break;
                }
            } catch (Exception e) {
                log.error("Can't execute script", e);
                respType.setStatus(StatusCodeType.FAILURE);
            }
        }

        List<ExtensibleAttribute> attributes = new ArrayList<ExtensibleAttribute>();
        if (attrNames instanceof List) {
            List<String> attrNamesList = (List<String>)attrNames;
            for (String name : attrNamesList) {
                attributes.add(new ExtensibleAttribute(name, ""));
            }
        } else if (attrNames instanceof Map) {
            Map<String, String> attrNamesMap = (Map<String,String>)attrNames;

            for (String name : attrNamesMap.keySet()) {
                attributes.add(new ExtensibleAttribute(name, "", attrNamesMap.get(name)));
            }
        }

        respType.setStatus(StatusCodeType.SUCCESS);
        respType.setAttributes(attributes);

        return respType;
    }
}
