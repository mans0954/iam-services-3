package org.openiam.spml2.spi.salesforce.command.base;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.connector.data.ConnectorConfiguration;
import org.openiam.spml2.util.msg.ResponseBuilder;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/16/13
 * Time: 11:54 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractModifySalesForceCommand<ProvisionObject extends GenericProvisionObject> extends AbstractSalesForceInsertCommand<ProvisionObject, ModifyRequestType<ProvisionObject>, ModifyResponseType> {
    @Override
    public ModifyResponseType execute(ModifyRequestType<ProvisionObject> modifyRequestType) throws ConnectorDataException {
        final ModifyResponseType response = new ModifyResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String targetID = modifyRequestType.getPsoID().getTargetID();
        ConnectorConfiguration configuration = this.getConfiguration(targetID, ConnectorConfiguration.class);

        final String objectId = modifyRequestType.getPsoID().getID();

        try {
            final List<ModificationType> modTypeList = modifyRequestType.getModification();
            if(CollectionUtils.isNotEmpty(modTypeList)) {
                for (final ModificationType mod : modTypeList) {
                    final List<ExtensibleObject> objectList = mod.getData().getAny();
                    insertOrUpdate(objectId, objectList, configuration.getManagedSys());
                }
            }
            return response;
        } catch(Throwable e) {
            log.error("Unkonwn error", e);
            throw new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
        }
    }
}
