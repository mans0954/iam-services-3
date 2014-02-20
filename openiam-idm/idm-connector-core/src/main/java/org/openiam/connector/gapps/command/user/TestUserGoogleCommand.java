package org.openiam.connector.gapps.command.user;

import org.openiam.connector.gapps.GoogleAgent;
import org.openiam.connector.gapps.command.base.AbstractGoogleAppsCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleObject;
import org.springframework.stereotype.Service;

@Service("testUserGoogleAppsCommand")
public class TestUserGoogleCommand<ExtObject extends ExtensibleObject> extends
        AbstractGoogleAppsCommand<RequestType<ExtObject>, ResponseType> {

    @Override
    public ResponseType execute(RequestType<ExtObject> request)
            throws ConnectorDataException {
        ResponseType response = new ResponseType();
        ManagedSysEntity mSys = managedSysService.getManagedSysById(request
                .getTargetID());
        String adminEmail = mSys.getUserId();
        String password = this.getPassword(mSys.getId());
        String domain = mSys.getHostUrl();
        try {
            GoogleAgent client = new GoogleAgent();
            client.getAllUsers(adminEmail, password, domain);
            response.setStatus(StatusCodeType.SUCCESS);
        } catch (Exception e) {
            response.setStatus(StatusCodeType.FAILURE);
        }
        return response;
    }

}
