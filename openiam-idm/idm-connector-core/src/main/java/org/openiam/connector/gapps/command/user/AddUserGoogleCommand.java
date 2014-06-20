package org.openiam.connector.gapps.command.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.gapps.GoogleAgent;
import org.openiam.connector.gapps.command.base.AbstractCrudGoogleAppsCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.provision.type.ExtensibleAttribute;
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
			String userId = agent.addUser(
					adminEmail,
					password,
					domain,
					this.extensibleUserToGoogle(
							crudRequest.getExtensibleObject(),
							crudRequest.getObjectIdentity(), domain));
			boolean isGroupsExists = this.addGroups(
					crudRequest.getExtensibleObject(), agent, adminEmail,
					password, domain, userId);

			ConnectorConfiguration configuration = super.getConfiguration(
					crudRequest.getTargetID(), ConnectorConfiguration.class);
			Resource res = configuration.getResource();
			String isProfileShared = "shared";
			String gamLocation = "/data/openiam/conf/gam/";
			if (res != null) {
				ResourceProp resprop = configuration.getResource()
						.getResourceProperty("IS_PROFILE_SHARED");
				if (resprop != null) {
					isProfileShared = resprop.getValue();
				}
				ResourceProp resprop2 = configuration.getResource()
						.getResourceProperty("GAM_LOCATION");
				if (resprop2 != null) {
					gamLocation = resprop2.getValue();
				}
			}

			String command = String.format(
					"python %sgam.py user %s profile %s", gamLocation, userId,
					isProfileShared);

			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(command);
			int exitVal = proc.exitValue();
			if (exitVal == 0) {
				log.info("Command: " + command
						+ " was executed succesfully. RetVal=" + exitVal);
			} else {
				log.info("Command: " + command
						+ " was executed with error. RetVal=" + exitVal);
			}

		} catch (Exception e) {
			throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
					e.getMessage());
		}
	}
}
