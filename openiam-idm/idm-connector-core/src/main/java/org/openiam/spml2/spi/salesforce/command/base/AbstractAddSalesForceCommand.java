package org.openiam.spml2.spi.salesforce.command.base;

import com.sforce.ws.ConnectionException;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
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
 * Date: 7/15/13
 * Time: 10:55 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractAddSalesForceCommand<ProvisionObject extends GenericProvisionObject> extends AbstractSalesForceInsertCommand<ProvisionObject, AddRequestType<ProvisionObject>, AddResponseType> {
    @Override
    public AddResponseType execute(AddRequestType<ProvisionObject> addRequestType) throws ConnectorDataException {
        final AddResponseType response = new AddResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String targetID = addRequestType.getTargetID();
        ConnectorConfiguration configuration = this.getConfiguration(targetID, ConnectorConfiguration.class);
        final String principalName = addRequestType.getPsoID().getID();
        try {

            final List<ExtensibleObject> objectList = addRequestType.getData().getAny();
            this.insertOrUpdate(principalName, objectList, configuration.getManagedSys());
            //com.sforce.soap.partner.sobject.SObject
            //partnerConnection.create(sObjects);
        }  catch(Throwable e) {
            log.error("Unkonwn error", e);
            throw new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
        }
        return response;
    }

}
