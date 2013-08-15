package org.openiam.connector.salesforce.command.user;

import com.sforce.ws.ConnectionException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.connector.salesforce.command.base.AbstractDeleteSalesForceCommand;
import org.openiam.connector.salesforce.dao.CallerDependentSalesForceDao;
import org.openiam.connector.salesforce.dao.SalesForceDao;
import org.openiam.connector.salesforce.exception.SalesForceDataIntegrityException;
import org.openiam.connector.salesforce.exception.SalesForcePersistException;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/16/13
 * Time: 10:10 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("deleteUserSalesForceCommand")
public class DeleteUserSalesForceCommand extends AbstractDeleteSalesForceCommand<ExtensibleUser> {
    @Override
    protected void deleteObject(String principalName, ManagedSysEntity managedSys) throws ConnectorDataException {
        try {
            final SalesForceDao dao = new CallerDependentSalesForceDao(managedSys.getUserId(), getDecryptedPassword(managedSys.getPswd()),
                                                                       managedSys.getConnectionString(), null);
            dao.deleteByUserName(principalName);
        } catch(SalesForcePersistException e) {
            log.error("Sales Force Persist Exception", e);
            throw new ConnectorDataException(ErrorCode.PERSIST_EXCEPTION, e.getMessage());
        } catch (ConnectionException e) {
            log.error("Connection Exception", e);
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
