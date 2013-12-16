package org.openiam.connector.gapps.command.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openiam.connector.gapps.command.base.AbstractGoogleAppsCommand;
import org.openiam.connector.linux.command.base.AbstractLinuxCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysRuleEntity;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.User;

@Service("testUserGoogleAppsCommand")
public class TestUserGoogleCommand<ExtObject extends ExtensibleObject> extends
        AbstractGoogleAppsCommand<RequestType<ExtObject>, ResponseType> {

    @Override
    public ResponseType execute(RequestType<ExtObject> request)
            throws ConnectorDataException {
        ResponseType response = new ResponseType();
        try {
            response.setStatus(StatusCodeType.SUCCESS);
            ManagedSysEntity mSys = managedSysService.getManagedSysById(request
                    .getTargetID());
            List<ManagedSysRuleEntity> rules = this.getRules(mSys);

            Directory dir = getGoogleAppsClient(rules);

            List<User> googleUsers = dir.users().list()
                    .setDomain(mSys.getHostUrl()).execute().getUsers();
        } catch (IOException e) {
            response.setStatus(StatusCodeType.FAILURE);
        }
        return response;
    }

}
