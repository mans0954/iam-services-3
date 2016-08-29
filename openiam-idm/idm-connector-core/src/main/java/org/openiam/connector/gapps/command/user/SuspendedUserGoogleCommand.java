package org.openiam.connector.gapps.command.user;

import org.openiam.connector.gapps.GoogleAgent;
import org.openiam.connector.gapps.command.base.AbstractGoogleAppsCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.SuspendResumeRequest;
import org.openiam.base.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Service;

import com.google.gdata.data.appsforyourdomain.generic.GenericEntry;

@Service("suspendUserGoogleAppsCommand")
public class SuspendedUserGoogleCommand extends
		AbstractGoogleAppsCommand<SuspendResumeRequest, ResponseType> {

	@Override
	public ResponseType execute(SuspendResumeRequest req)
			throws ConnectorDataException {
		ResponseType responseType = new ResponseType();
        responseType.setRequestID(req.getRequestID());
        responseType.setStatus(StatusCodeType.SUCCESS);
		ManagedSysEntity mSys = managedSysService.getManagedSysById(req
				.getTargetID());
		String adminEmail = mSys.getUserId();
		String password = this.getPassword(mSys.getId());
		String domain = mSys.getHostUrl();
		try {
			GoogleAgent agent = new GoogleAgent();
			GenericEntry getUser = agent.getUser(adminEmail, password, domain,
					req.getObjectIdentity());
			getUser.addProperty("isSuspended", "true");
			agent.updateUser(adminEmail, password, domain,
					getUser.getAllProperties(), req.getObjectIdentity());
            return responseType;
		} catch (Exception e) {
			throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
					e.getMessage());
		}
	}
}
