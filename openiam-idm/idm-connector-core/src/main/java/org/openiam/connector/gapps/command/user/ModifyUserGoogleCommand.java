package org.openiam.connector.gapps.command.user;

import org.openiam.connector.gapps.GoogleAgent;
import org.openiam.connector.gapps.command.base.AbstractCrudGoogleAppsCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

@Service("modifyUserGoogleAppsCommand")
public class ModifyUserGoogleCommand extends
        AbstractCrudGoogleAppsCommand<ExtensibleUser> {

    @Override
    protected void performObjectOperation(
            CrudRequest<ExtensibleUser> crudRequest, ManagedSysEntity managedSys)
            throws ConnectorDataException {
        ManagedSysEntity mSys = managedSysService.getManagedSysById(crudRequest
                .getTargetID());
        String adminEmail = mSys.getUserId();
        String password = this.getPassword(mSys.getManagedSysId());
        String domain = mSys.getHostUrl();
        try {
            GoogleAgent agent = new GoogleAgent();
            agent.updateUser(
                    adminEmail,
                    password,
                    domain,
                    this.extensibleUserToGoogle(
                            crudRequest.getExtensibleObject(),
                            crudRequest.getObjectIdentity(), domain),
                    crudRequest.getObjectIdentity());
        } catch (Exception e) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                    e.getMessage());
        }
    }
}
