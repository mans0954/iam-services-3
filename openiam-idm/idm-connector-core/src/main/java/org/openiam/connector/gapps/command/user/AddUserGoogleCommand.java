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

@Service("addUserGoogleAppsCommand")
public class AddUserGoogleCommand extends
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
			GenericEntry e = agent.addUser(
					adminEmail,
					password,
					domain,
					this.extensibleUserToGoogle(
							crudRequest.getExtensibleObject(),
							crudRequest.getObjectIdentity(), domain));
			this.addGroups(crudRequest.getExtensibleObject(), agent,
					adminEmail, password, domain, e.getProperty("userEmail"));

			ConnectorConfiguration configuration = super.getConfiguration(
					crudRequest.getTargetID(), ConnectorConfiguration.class);
			// Run Gam commands
			this.runGamCommands("ADD", configuration.getResource(), e);
			// String isProfileShared = "shared";
			// String gamLocation = System.getProperty("confpath", "/data/openiam").concat("/conf/gam/");
			// if (res != null) {
			// ResourceProp resprop = configuration.getResource()
			// .getResourceProperty("IS_PROFILE_SHARED");
			// if (resprop != null) {
			// isProfileShared = resprop.getValue();
			// }
			// ResourceProp resprop2 = configuration.getResource()
			// .getResourceProperty("GAM_LOCATION");
			// if (resprop2 != null) {
			// gamLocation = resprop2.getValue();
			// }
			// }
			//
			// String command = String.format(
			// "python %sgam.py user %s profile %s", gamLocation, userId,
			// isProfileShared);
			//
			// Runtime rt = Runtime.getRuntime();
			// Process proc = rt.exec(command);
			//
			// int exitVal = proc.waitFor();
			// if (exitVal == 0) {
			// log.info("Command: " + command
			// + " was executed succesfully. RetVal=" + exitVal);
			// } else {
			// log.info("Command: " + command
			// + " was executed with error. RetVal=" + exitVal);
			// }

		} catch (Exception e) {
			throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
					e.getMessage());
		}
	}
}
