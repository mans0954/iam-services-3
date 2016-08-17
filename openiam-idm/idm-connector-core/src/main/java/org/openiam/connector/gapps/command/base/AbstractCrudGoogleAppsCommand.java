package org.openiam.connector.gapps.command.base;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.gapps.GoogleAgent;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.CrudRequest;
import org.openiam.base.response.ObjectResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;

import com.google.gdata.util.ServiceException;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 8/6/13 Time: 10:49 PM To
 * change this template use File | Settings | File Templates.
 */
public abstract class AbstractCrudGoogleAppsCommand<ExtObject extends ExtensibleObject> extends
	AbstractGoogleAppsCommand<CrudRequest<ExtObject>, ObjectResponse> {
    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> crudRequest) throws ConnectorDataException {
	ObjectResponse respType = new ObjectResponse();
	respType.setStatus(StatusCodeType.SUCCESS);

	ConnectorConfiguration config = getConfiguration(crudRequest.getTargetID(), ConnectorConfiguration.class);
	performObjectOperation(crudRequest, config.getManagedSys());
	return respType;
    }

    protected boolean addGroups(ExtensibleObject user, GoogleAgent agent, String adminEmail, String password,
	    String domain, String userId) throws
			IOException, ServiceException {
	boolean res = false;
	for (ExtensibleAttribute ea : user.getAttributes()) {
	    if ("groups".equals(ea.getName())) {
		List<String> names = ea.getValueList();
		for (String name : names) {
		    Map<String, String> groupMap = new HashMap<String, String>();
		    groupMap.put("groupName", name);
		    groupMap.put("groupId", name.replace(" ", "_") + "@" + domain);
		    agent.addGroup(adminEmail, password, domain, groupMap);
		    agent.addUserToGroup(adminEmail, password, domain, groupMap.get("groupId"), userId);
		    res = true;
		}
	    }
	}
	return res;
    }

    protected abstract void performObjectOperation(CrudRequest<ExtObject> crudRequest, ManagedSysEntity managedSys)
	    throws ConnectorDataException;
}
