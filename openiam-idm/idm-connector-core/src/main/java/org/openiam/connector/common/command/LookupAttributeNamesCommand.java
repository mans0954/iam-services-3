package org.openiam.connector.common.command;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.ldap.command.base.AbstractLdapCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.LookupAttributeResponse;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
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
        ConnectorConfiguration config = getConfiguration(lookupRequest.getTargetID(), ConnectorConfiguration.class);
        ManagedSysEntity mngSys = config.getManagedSys();
        try {

            Object attrNames = null;
            if (StringUtils.isNotBlank(mngSys.getAttributeNamesLookup())) {
                Map<String, Object> bindingMap = new HashMap<String, Object>();
                bindingMap.put("managedSys", mngSys);
                attrNames = scriptRunner.execute(bindingMap, mngSys.getAttributeNamesLookup());
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

        } catch (ScriptEngineException e) {
            log.error("Can't execute script", e);
            respType.setStatus(StatusCodeType.FAILURE);
        }

        return respType;
    }
}
