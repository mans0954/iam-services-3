package org.openiam.connector.gapps.command.user;

import java.io.IOException;
import java.util.List;

import org.openiam.connector.gapps.command.base.AbstractGoogleAppsCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysRuleEntity;
import org.springframework.stereotype.Service;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.User;

@Service("resumeUserGoogleAppsCommand")
public class ResumeUserGoogleCommand extends
        AbstractGoogleAppsCommand<SuspendResumeRequest, ResponseType> {

    @Override
    public ResponseType execute(SuspendResumeRequest passwordReq)
            throws ConnectorDataException {
        ResponseType responseType = new ResponseType();
        try {
            ManagedSysEntity mSys = managedSysService
                    .getManagedSysById(passwordReq.getTargetID());
            List<ManagedSysRuleEntity> rules = this.getRules(mSys);

            responseType.setRequestID(passwordReq.getRequestID());
            responseType.setStatus(StatusCodeType.SUCCESS);
            Directory dir = getGoogleAppsClient(rules);

            User user = dir.users().get(passwordReq.getObjectIdentity())
                    .execute();
            user.setSuspended(false);
            dir.users().update(user.getId(), user);
        } catch (IOException e) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                    e.getMessage());
        }
        return responseType;
    }
}
