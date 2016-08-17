package org.openiam.connector.scim.command.base;

import java.net.HttpURLConnection;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.RequestType;
import org.openiam.base.response.ObjectResponse;
import org.openiam.provision.type.ExtensibleObject;
import org.springframework.stereotype.Service;

@Service("testScimCommand")
public class TestScimCommand<ExtObject extends ExtensibleObject> extends
		AbstractScimCommand<RequestType<ExtObject>, ObjectResponse> {
	@Override
	public ObjectResponse execute(RequestType<ExtObject> crudRequest)
			throws ConnectorDataException {
		final ObjectResponse response = new ObjectResponse();
		response.setStatus(StatusCodeType.SUCCESS);
		ConnectorConfiguration config = getConfiguration(
				crudRequest.getTargetID(), ConnectorConfiguration.class);
		HttpURLConnection con = super.getConnection(config.getManagedSys(),
				"/v1/Users");
		con.disconnect();
		return response;
	}
}
