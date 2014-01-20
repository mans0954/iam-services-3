package org.openiam.connector.gapps.command.user;

import org.openiam.connector.gapps.GoogleAgent;
import org.openiam.connector.gapps.command.base.AbstractGoogleAppsCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Service;

import com.google.gdata.data.appsforyourdomain.generic.GenericEntry;

@Service("setPasswordGoogleAppsCommand")
public class SetPasswordUserGoogleCommand extends
        AbstractGoogleAppsCommand<PasswordRequest, ResponseType> {

    @Override
    public ResponseType execute(PasswordRequest passwordReq)
            throws ConnectorDataException {
        ResponseType responseType = new ResponseType();
        ManagedSysEntity mSys = managedSysService.getManagedSysById(passwordReq
                .getTargetID());
        String adminEmail = mSys.getUserId();
        String password = this.getPassword(mSys.getManagedSysId());
        String domain = mSys.getHostUrl();
        try {
            GoogleAgent agent = new GoogleAgent();
            GenericEntry getUser = agent.getUser(adminEmail, password, domain,
                    passwordReq.getObjectIdentity());
            getUser.getAllProperties().put("password",
                    passwordReq.getPassword());
            agent.updateUser(adminEmail, password, domain,
                    getUser.getAllProperties(), passwordReq.getObjectIdentity());
        } catch (Exception e) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                    e.getMessage());
        }

        return responseType;
    }
}