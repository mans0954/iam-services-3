package org.openiam.connector.salesforce.command.user;

import com.sforce.ws.ConnectionException;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.SuspendResumeRequest;
import org.openiam.base.response.ResponseType;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.salesforce.command.base.AbstractSalesforceCommand;
import org.openiam.connector.salesforce.dao.CallerDependentSalesForceDao;
import org.openiam.connector.salesforce.dao.SalesForceDao;
import org.openiam.connector.salesforce.exception.SalesForceDataIntegrityException;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/17/13
 * Time: 1:06 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("suspendSalesForceCommand")
public class SuspendSalesForceCommand extends AbstractSalesforceCommand<SuspendResumeRequest, ResponseType> {
    @Override
    public ResponseType execute(SuspendResumeRequest suspendRequestType) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = suspendRequestType.getObjectIdentity();
        final String targetID = suspendRequestType.getTargetID();

        ConnectorConfiguration configuration = this.getConfiguration(targetID, ConnectorConfiguration.class);

        try {
            final SalesForceDao dao = new CallerDependentSalesForceDao(configuration.getManagedSys().getUserId(),
                                                                        getDecryptedPassword(configuration.getManagedSys().getPswd()),
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
