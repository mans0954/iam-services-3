package org.openiam.spml2.spi.orcl.command.base;

import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.*;

import java.sql.Connection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/11/13
 * Time: 10:37 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractAddOracleCommand<ProvisionObject extends GenericProvisionObject> extends AbstractOracleCommand<AddRequestType<ProvisionObject>, AddResponseType> {
    @Override
    public AddResponseType execute(AddRequestType<ProvisionObject> addRequestType) throws ConnectorDataException {
        final AddResponseType response = new AddResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String targetID = addRequestType.getTargetID();
        final ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);
        String resourceId = getResourceId(targetID, managedSys);


        final String principalName = addRequestType.getPsoID().getID();
        if(principalName == null)
            throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "No principal sent");

        final List<ExtensibleObject> objectList = addRequestType.getData().getAny();

        if(log.isDebugEnabled()) {
            log.debug(String.format("ExtensibleObject in Add Request=%s", objectList));
        }

        final List<AttributeMapEntity> attributeMap = attributeMaps(resourceId);

        Connection con = this.getConnection(managedSys);
        try {
            addObject(principalName,objectList,attributeMap,  con);
            return response;
        } catch (ConnectorDataException e) {
            log.error(e.getMessage(), e);
            throw  e;
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
        } finally {
            this.closeConnection(con);
        }
    }

    protected abstract void addObject(String principalName, List<ExtensibleObject> objectList, List<AttributeMapEntity> attributeMap, Connection con) throws ConnectorDataException;
}
