package org.openiam.spml2.spi.salesforce.command.base;

import com.sforce.ws.ConnectionException;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.common.data.ConnectorConfiguration;
import org.openiam.spml2.spi.salesforce.dao.CallerDependentSalesForceDao;
import org.openiam.spml2.spi.salesforce.dao.SalesForceDao;
import org.openiam.spml2.spi.salesforce.exception.SalesForceDataIntegrityException;
import org.openiam.spml2.spi.salesforce.exception.SalesForcePersistException;
import org.openiam.spml2.util.msg.ResponseBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/16/13
 * Time: 7:45 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDeleteSalesForceCommand<ProvisionObject extends GenericProvisionObject> extends AbstractSalesforceCommand<DeleteRequestType<ProvisionObject>, ResponseType>  {
    @Override
    public ResponseType execute(DeleteRequestType<ProvisionObject> deleteRequestType) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = deleteRequestType.getPsoID().getID();
        final String targetID = deleteRequestType.getPsoID().getTargetID();
        ConnectorConfiguration configuration = this.getConfiguration(targetID, ConnectorConfiguration.class);

        deleteObject(principalName, configuration.getManagedSys());
        return response;
    }

    protected abstract void deleteObject(String principalName, ManagedSysEntity managedSys) throws ConnectorDataException;
}
