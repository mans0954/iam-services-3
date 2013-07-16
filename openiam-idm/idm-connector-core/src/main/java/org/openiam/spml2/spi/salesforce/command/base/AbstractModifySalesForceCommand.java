package org.openiam.spml2.spi.salesforce.command.base;

import com.sforce.ws.ConnectionException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.common.data.ConnectorConfiguration;
import org.openiam.spml2.spi.salesforce.exception.SalesForceDataIntegrityException;
import org.openiam.spml2.spi.salesforce.exception.SalesForcePersistException;
import org.openiam.spml2.util.msg.ResponseBuilder;

import java.text.ParseException;
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
