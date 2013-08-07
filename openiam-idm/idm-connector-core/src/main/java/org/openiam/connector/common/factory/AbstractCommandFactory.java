package org.openiam.connector.common.factory;

import org.openiam.provision.type.ExtensibleObjectType;
import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.common.command.ConnectorCommand;

public abstract  class AbstractCommandFactory {
    protected static final String ERROR_PATTERN = "Unsupported Operation: '%s' for object type: '%s' in %s connector";

    public abstract ConnectorCommand getConnectorCommand(CommandType commandType,  ExtensibleObjectType extensibleObjectType) throws ConnectorDataException;
}
