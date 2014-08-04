package org.openiam.provision.service;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.provision.dto.ProvisionActionEvent;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import java.util.HashMap;
import java.util.Map;

@WebService(endpointInterface = "org.openiam.provision.service.ProvisionActionEventService", targetNamespace = "http://www.openiam.org/service/provision", portName = "ProvisionActionEventServicePort", serviceName = "ProvisionActionEventService")
@Component("provisionActionEventService")
public class ProvisionActionEventServiceImpl implements ProvisionActionEventService {

    protected static final Log log = LogFactory.getLog(ProvisionActionEventServiceImpl.class);

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;

    @Autowired
    private String eventProcessor = null;

    @Override
    public void add(ProvisionActionEvent event) {
        Map<String, Object> bindingMap = new HashMap<String, Object>();
        ProvisionServiceEventProcessor eventProcessorScript = getEventProcessor(bindingMap, eventProcessor);
        if (eventProcessorScript != null) {
            eventProcessorScript.process(event);
        }
    }

    private ProvisionServiceEventProcessor getEventProcessor(Map<String, Object> bindingMap, String scriptName) {
        if (StringUtils.isNotBlank(scriptName)) {
            try {
                return (ProvisionServiceEventProcessor) scriptRunner.instantiateClass(bindingMap, scriptName);
            } catch (Exception ce) {
                log.error(ce);
            }
        }
        return null;
    }

}
