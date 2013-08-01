package org.openiam.spml2.spi.orcl.command.base;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.dto.GenericProvisionObject;

import java.sql.Connection;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/12/13
 * Time: 12:27 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDeleteOracleCommand<ProvisionObject extends GenericProvisionObject> extends AbstractOracleCommand<DeleteRequestType<ProvisionObject>, ResponseType> {
    @Override
    public ResponseType execute(DeleteRequestType<ProvisionObject> deleteRequestType) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String dataId = deleteRequestType.getPsoID().getID();
        final String targetID = deleteRequestType.getPsoID().getTargetID();

        ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);

        Connection con = getConnection(managedSys);
        try {
            deleteObject(dataId, con);
            return response;
        }  catch(Throwable e) {
            log.error(e.getMessage(),e);
            throw  new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
        } finally {
           this.closeConnection(con);
        }
    }

    protected abstract void deleteObject(String dataId,  Connection con)throws ConnectorDataException;
}
