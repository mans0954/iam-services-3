package org.openiam.spml2.spi.jdbc.command.base;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.jdbc.command.data.AppTableConfiguration;

import java.sql.Connection;
import java.util.List;

public abstract class AbstractAddAppTableCommand<ProvisionObject extends GenericProvisionObject> extends AbstractAppTableCommand<AddRequestType<ProvisionObject>, AddResponseType> {
    protected static final String INSERT_SQL = "INSERT INTO %s (%s) VALUES (%s)";

    @Override
    public AddResponseType execute(AddRequestType<ProvisionObject> addRequestType) throws ConnectorDataException {
        final AddResponseType response = new AddResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String targetID = addRequestType.getTargetID();
        AppTableConfiguration configuration = this.getConfiguration(targetID);

        final String principalName = addRequestType.getPsoID().getID();
        final List<ExtensibleObject> objectList = addRequestType.getData().getAny();

        if(log.isDebugEnabled()) {
            log.debug(String.format("ExtensibleObject in Add Request=%s", objectList));
        }

        Connection con = getConnection(configuration.getManagedSys());
        try {
            addObject(con, principalName, objectList, configuration.getTableName());
            return response;
        } catch (ConnectorDataException e) {
            log.error(e.getMessage(),e);
            throw e;
        } finally {
            this.closeConnection(con);
        }
    }

    protected abstract void addObject(Connection con, String principalName, List<ExtensibleObject> objectList, String tableName) throws ConnectorDataException;
}
