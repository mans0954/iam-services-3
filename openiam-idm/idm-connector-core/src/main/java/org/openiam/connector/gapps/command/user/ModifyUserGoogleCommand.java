package org.openiam.connector.gapps.command.user;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.gapps.GoogleAgent;
import org.openiam.connector.gapps.GoogleUtils;
import org.openiam.connector.gapps.command.base.AbstractCrudGoogleAppsCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.request.CrudRequest;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
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
		String newIdentity = crudRequest.getObjectIdentity();
		String oldIdentity = null;
		ExtensibleObject user = crudRequest.getExtensibleObject();
		if (user != null & user.getAttributes() != null) {
			for (ExtensibleAttribute ea : user.getAttributes()) {
				if ("ORIG_IDENTITY".equals(ea.getName())) {
					oldIdentity = ea.getValue();
					break;
				}
			}
		}

		try {
			GenericEntry e = null;
			GoogleAgent agent = new GoogleAgent();

			if (oldIdentity == null || oldIdentity.equals(newIdentity)) {
				e = agent.getUser(adminEmail, password, domain, newIdentity);
			} else {
				e = agent.getUser(adminEmail, password, domain, oldIdentity);
			}
			if (e == null) {
				throw new ConnectorDataException(ErrorCode.NO_SUCH_OBJECT,
						"Can't find such user in Google!");
			}
			e = new GenericEntry();
			e.addProperties(this.extensibleUserToGoogle(crudRequest
					.getExtensibleObject(), oldIdentity == null ? newIdentity
					: oldIdentity, domain));

			if (oldIdentity == null || oldIdentity.equals(newIdentity)) {
				e = agent.updateUser(adminEmail, password, domain, e);
			} else {
				e.addProperty("newEmail",
						GoogleUtils.makeGoogleId(newIdentity, domain));
				e = agent.updateUser(adminEmail, password, domain, e,
						oldIdentity, true);
			}

			ConnectorConfiguration configuration = super.getConfiguration(
					crudRequest.getTargetID(), ConnectorConfiguration.class);
			this.runGamCommands("UPDATE", configuration.getResource(), e);

		} catch (Exception e) {
			throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
					e.getMessage());
		}
	}
}
