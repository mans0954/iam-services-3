package org.openiam.connector.script.command.base;

import org.openiam.connector.common.command.AbstractCommand;
import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.request.RequestType;
import org.openiam.base.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/17/13 Time: 3:34 PM To
 * change this template use File | Settings | File Templates.
 */
public abstract class AbstractScriptCommand<Request extends RequestType, Response extends ResponseType> extends
        AbstractCommand<Request, Response> {
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    @Override
    public Response execute(Request request) throws ConnectorDataException {
        return runCommand(request.getTargetID(), request);
    }

    protected Response runCommand(String targetID, Request request) throws ConnectorDataException {
        try {
            ConnectorConfiguration configuration = this.getConfiguration(targetID, ConnectorConfiguration.class);
            String connectorPath = "/connector/" + getFileName(configuration.getManagedSys());
            AbstractCommand<Request, Response> cmd = (AbstractCommand<Request, Response>) scriptRunner
                    .instantiateClass(null, connectorPath);
            Response resp = cmd.execute(request);
            return resp;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "Could not perform operation");
        }
    }

    protected abstract CommandType getCommandType();

    protected abstract String getFileName(ManagedSysEntity msys) throws Exception;
}
