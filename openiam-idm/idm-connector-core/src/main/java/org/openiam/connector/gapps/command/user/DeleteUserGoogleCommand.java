package org.openiam.connector.gapps.command.user;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.gapps.GoogleAgent;
import org.openiam.connector.gapps.command.base.AbstractCrudGoogleAppsCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.request.CrudRequest;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

import com.google.gdata.data.appsforyourdomain.generic.GenericEntry;

@Service("deleteUserGoogleAppsCommand")
public class DeleteUserGoogleCommand extends
		AbstractCrudGoogleAppsCommand<ExtensibleUser> {

	@Override
	protected void performObjectOperation(
			CrudRequest<ExtensibleUser> crudRequest, ManagedSysEntity managedSys)
			throws ConnectorDataException {
		ManagedSysEntity mSys = managedSysService.getManagedSysById(crudRequest
				.getTargetID());
		String adminEmail = mSys.getUserId();
		String password = this.getPassword(mSys.getId());
		String domain = mSys.getHostUrl();
		try {
			GoogleAgent agent = new GoogleAgent();
			ConnectorConfiguration configuration = super.getConfiguration(
					crudRequest.getTargetID(), ConnectorConfiguration.class);
			GenericEntry e = new GenericEntry();
			e.addProperty("userEmail", crudRequest.getObjectIdentity());
			this.runGamCommands("DELETE", configuration.getResource(), e);

			agent.deleteUser(adminEmail, password, domain,
					crudRequest.getObjectIdentity());
		} catch (Exception e) {
			throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
					e.getMessage());
		}
	}
}
