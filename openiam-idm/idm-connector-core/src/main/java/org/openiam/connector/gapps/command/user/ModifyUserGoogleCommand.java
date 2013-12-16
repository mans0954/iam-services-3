package org.openiam.connector.gapps.command.user;

import java.util.List;

import org.openiam.connector.gapps.command.base.AbstractCrudGoogleAppsCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysRuleEntity;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.User;

@Service("modifyUserGoogleAppsCommand")
public class ModifyUserGoogleCommand extends
        AbstractCrudGoogleAppsCommand<ExtensibleUser> {

    @Override
    protected void performObjectOperation(
            CrudRequest<ExtensibleUser> crudRequest, ManagedSysEntity managedSys)
            throws ConnectorDataException {
        try {
            List<ManagedSysRuleEntity> rules = this.getRules(managedSys);

            Directory dir = this.getGoogleAppsClient(rules);
            User googleUser = new User();
            this.convertToGoogleUser(googleUser,
                    crudRequest.getExtensibleObject(), rules);

            googleUser = dir
                    .users()
                    .update(crudRequest.getExtensibleObject().getObjectId(),
                            googleUser).execute();
        } catch (Exception e) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                    e.getMessage());
        }
    }
}
