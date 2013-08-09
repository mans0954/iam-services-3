package org.openiam.connector.salesforce.command.user;

import com.sforce.ws.ConnectionException;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.salesforce.command.base.AbstractSalesforceCommand;
import org.openiam.connector.salesforce.dao.CallerDependentSalesForceDao;
import org.openiam.connector.salesforce.dao.SalesForceDao;
import org.openiam.connector.salesforce.exception.SalesForceDataIntegrityException;
import org.openiam.connector.salesforce.exception.SalesForcePersistException;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/17/13
 * Time: 12:12 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("resumeSalesForceCommand")
public class ResumeSalesForceCommand  extends AbstractSalesforceCommand<SuspendResumeRequest, ResponseType> {
    @Override
    public ResponseType execute(SuspendResumeRequest resumeRequestType) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = resumeRequestType.getObjectIdentity();
        final String targetID = resumeRequestType.getTargetID();
        ConnectorConfiguration configuration = this.getConfiguration(targetID, ConnectorConfiguration.class);

        try {
            final SalesForceDao dao = new CallerDependentSalesForceDao(configuration.getManagedSys().getUserId(),
                                                                       getDecryptedPassword(configuration.getManagedSys().getUserId(), configuration.getManagedSys().getPswd()),
                                                                       configuration.getManagedSys().getConnectionString(), null);
            dao.undeleteByUserName(principalName);
            return response;
        } catch (SalesForcePersistException e) {
            log.error(e.getMessage(),e);
            throw new ConnectorDataException(ErrorCode.PERSIST_EXCEPTION, e.getMessage());
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
