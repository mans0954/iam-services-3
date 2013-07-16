package org.openiam.spml2.spi.salesforce.command.user;

import com.sforce.ws.ConnectionException;
import org.apache.commons.lang.StringUtils;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.msg.StatusCodeType;
import org.openiam.spml2.msg.suspend.ResumeRequestType;
import org.openiam.spml2.spi.common.data.ConnectorConfiguration;
import org.openiam.spml2.spi.salesforce.command.base.AbstractSalesforceCommand;
import org.openiam.spml2.spi.salesforce.dao.CallerDependentSalesForceDao;
import org.openiam.spml2.spi.salesforce.dao.SalesForceDao;
import org.openiam.spml2.spi.salesforce.exception.SalesForceDataIntegrityException;
import org.openiam.spml2.spi.salesforce.exception.SalesForcePersistException;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/17/13
 * Time: 12:12 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("resumeSalesForceCommand")
public class ResumeSalesForceCommand  extends AbstractSalesforceCommand<ResumeRequestType, ResponseType> {
    @Override
    public ResponseType execute(ResumeRequestType resumeRequestType) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = resumeRequestType.getPsoID().getID();
        final String targetID = resumeRequestType.getPsoID().getTargetID();
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
