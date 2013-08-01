package org.openiam.spml2.spi.script.command.base;

import org.openiam.script.ScriptIntegration;
import org.openiam.spml2.interf.ConnectorService;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.RequestType;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.connector.common.command.AbstractCommand;
import org.openiam.connector.data.ConnectorConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/18/13
 * Time: 12:55 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseScriptCommand<Request extends RequestType, Response extends ResponseType> extends AbstractScriptCommand<Request, Response> {
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    protected  Response runCommand(String targetID, Request request) throws ConnectorDataException{
        try {
            ConnectorConfiguration configuration = this.getConfiguration(targetID, ConnectorConfiguration.class);

            String connectorPath = "/connector/" + configuration.getManagedSys().getName() + ".groovy";

            Map<String, Object> bindingMap = new HashMap<String, Object>();
            bindingMap.put("managedSys", configuration.getManagedSys());
            AbstractCommand cmd =  (AbstractCommand) scriptRunner.instantiateClass(bindingMap, connectorPath);

            return (Response)cmd.execute(request);
        } catch (IOException e) {
           log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "Could not perform operation");
        }
    }
}
