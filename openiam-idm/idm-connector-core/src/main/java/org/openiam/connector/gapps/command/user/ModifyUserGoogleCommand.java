package org.openiam.connector.gapps.command.user;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.gapps.GoogleAgent;
import org.openiam.connector.gapps.command.base.AbstractCrudGoogleAppsCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

import com.google.gdata.data.appsforyourdomain.generic.GenericEntry;

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
		String password = this.getPassword(mSys.getId());
		String domain = mSys.getHostUrl();
		try {
			GoogleAgent agent = new GoogleAgent();
			GenericEntry e = agent.updateUser(
					adminEmail,
					password,
					domain,
					this.extensibleUserToGoogle(
							crudRequest.getExtensibleObject(),
							crudRequest.getObjectIdentity(), domain),
					crudRequest.getObjectIdentity());
			ConnectorConfiguration configuration = super.getConfiguration(
					crudRequest.getTargetID(), ConnectorConfiguration.class);
			this.runGamCommands("UPDATE", configuration.getResource(), e);

		} catch (Exception e) {
			throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
					e.getMessage());
		}
	}
}
