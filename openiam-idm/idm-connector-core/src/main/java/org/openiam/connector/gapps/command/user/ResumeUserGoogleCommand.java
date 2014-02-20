package org.openiam.connector.gapps.command.user;

import org.openiam.connector.gapps.GoogleAgent;
import org.openiam.connector.gapps.command.base.AbstractGoogleAppsCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Service;

import com.google.gdata.data.appsforyourdomain.generic.GenericEntry;

@Service("resumeUserGoogleAppsCommand")
public class ResumeUserGoogleCommand extends
        AbstractGoogleAppsCommand<SuspendResumeRequest, ResponseType> {

    @Override
    public ResponseType execute(SuspendResumeRequest req)
            throws ConnectorDataException {
        ResponseType responseType = new ResponseType();
        ManagedSysEntity mSys = managedSysService.getManagedSysById(req
                .getTargetID());
        String adminEmail = mSys.getUserId();
        String password = this.getPassword(mSys.getId());
        String domain = mSys.getHostUrl();
        try {
            GoogleAgent agent = new GoogleAgent();
            GenericEntry getUser = agent.getUser(adminEmail, password, domain,
                    req.getObjectIdentity());
            getUser.getAllProperties().put("isSuspended", "false");
            agent.updateUser(adminEmail, password, domain,
                    getUser.getAllProperties(), req.getObjectIdentity());
        } catch (Exception e) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                    e.getMessage());
        }
        return responseType;
    }
}
