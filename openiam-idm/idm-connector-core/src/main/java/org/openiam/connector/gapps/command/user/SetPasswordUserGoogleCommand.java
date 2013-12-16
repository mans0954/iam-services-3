package org.openiam.connector.gapps.command.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.mvel2.optimizers.impl.refl.nodes.ArrayLength;
import org.openiam.connector.gapps.command.base.AbstractGoogleAppsCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.request.SearchRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysRuleEntity;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.User;

@Service("setPasswordGoogleAppsCommand")
public class SetPasswordUserGoogleCommand extends
        AbstractGoogleAppsCommand<PasswordRequest, ResponseType> {

    @Override
    public ResponseType execute(PasswordRequest passwordReq)
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
            user.setPassword(passwordReq.getPassword());
            dir.users().update(user.getId(), user);
        } catch (IOException e) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                    e.getMessage());
        }
        return responseType;
    }
}
