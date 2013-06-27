package org.openiam.spml2.spi.common.factory;

import org.openiam.provision.dto.ProvisionObjectType;
import org.openiam.spml2.constants.CommandType;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.spi.common.ConnectorCommand;

public abstract  class AbstractCommandFactory {
    protected static final String ERROR_PATTERN = "Unsupported Operation: '%s' for object type: '%s' in %s connector";

    public abstract ConnectorCommand getConnectorCommand(CommandType commandType, ProvisionObjectType provisionObjectType) throws ConnectorDataException;
}
