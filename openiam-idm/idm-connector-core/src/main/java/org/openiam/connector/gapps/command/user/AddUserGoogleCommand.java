package org.openiam.connector.gapps.command.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openiam.connector.gapps.GoogleAgent;
import org.openiam.connector.gapps.command.base.AbstractCrudGoogleAppsCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

import com.google.gdata.data.appsforyourdomain.generic.GenericEntry;

@Service("addUserGoogleAppsCommand")
public class AddUserGoogleCommand extends AbstractCrudGoogleAppsCommand<ExtensibleUser> {

    @Override
    protected void performObjectOperation(CrudRequest<ExtensibleUser> crudRequest, ManagedSysEntity managedSys)
	    throws ConnectorDataException {
	ManagedSysEntity mSys = managedSysService.getManagedSysById(crudRequest.getTargetID());
	String adminEmail = mSys.getUserId();
	String password = this.getPassword(mSys.getManagedSysId());
	String domain = mSys.getHostUrl();

	try {
	    GoogleAgent agent = new GoogleAgent();
	    String userId = agent.addUser(adminEmail, password, domain, this.extensibleUserToGoogle(
		    crudRequest.getExtensibleObject(), crudRequest.getObjectIdentity(), domain));
	    boolean isGroupsExists = this.addGroups(crudRequest.getExtensibleObject(), agent, adminEmail, password,
		    domain, userId);
	    
	} catch (Exception e) {
	    throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
	}
    }
}
