package org.openiam.connector.script.command.base;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.script.ScriptIntegration;
import org.openiam.connector.common.command.AbstractCommand;
import org.openiam.spml2.constants.CommandType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/17/13
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractScriptCommand<Request extends RequestType, Response extends ResponseType> extends AbstractCommand<Request, Response> {
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    @Override
    public Response execute(Request request) throws ConnectorDataException {
        return runCommand(request.getTargetID(), request);
    }

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

    protected abstract CommandType getCommandType();
}
