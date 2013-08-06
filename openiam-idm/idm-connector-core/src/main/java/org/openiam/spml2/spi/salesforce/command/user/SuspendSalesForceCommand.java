package org.openiam.spml2.spi.salesforce.command.user;

import com.sforce.ws.ConnectionException;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.msg.StatusCodeType;
import org.openiam.spml2.msg.suspend.SuspendRequestType;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.spml2.spi.salesforce.command.base.AbstractSalesforceCommand;
import org.openiam.spml2.spi.salesforce.dao.CallerDependentSalesForceDao;
import org.openiam.spml2.spi.salesforce.dao.SalesForceDao;
import org.openiam.spml2.spi.salesforce.exception.SalesForceDataIntegrityException;
import org.openiam.spml2.util.msg.ResponseBuilder;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/17/13
 * Time: 1:06 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("suspendSalesForceCommand")
public class SuspendSalesForceCommand extends AbstractSalesforceCommand<SuspendRequestType, ResponseType> {
    @Override
    public ResponseType execute(SuspendRequestType suspendRequestType) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = suspendRequestType.getPsoID().getID();
        final String targetID = suspendRequestType.getPsoID().getTargetID();

        ConnectorConfiguration configuration = this.getConfiguration(targetID, ConnectorConfiguration.class);

        try {
            final SalesForceDao dao = new CallerDependentSalesForceDao(configuration.getManagedSys().getUserId(),
                                                                        getDecryptedPassword(configuration.getManagedSys().getUserId(), configuration.getManagedSys().getPswd()),
                                                                        configuration.getManagedSys().getConnectionString(), null);
            dao.deleteByUserName(principalName);
            return response;
        } catch (ConnectionException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.AUTHENTICATION_FAILED, e.getMessage());
        } catch(SalesForceDataIntegrityException e) {
            log.error("SalesForce Exception", e);
            throw new ConnectorDataException(ErrorCode.INVALID_ATTRIBUTE, e.getMessage());
        } catch(Throwable e) {
            log.error("Unkonwn error", e);
            throw new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
        }
    }
}
