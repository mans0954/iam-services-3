package org.openiam.connector.rest.command.base;

import java.io.IOException;
import java.net.HttpURLConnection;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.RequestType;
import org.openiam.base.response.ObjectResponse;
import org.openiam.provision.type.ExtensibleObject;
import org.springframework.stereotype.Service;

@Service("testRestCommand")
public class TestRestCommand<ExtObject extends ExtensibleObject> extends
		AbstractRestCommand<RequestType<ExtObject>, ObjectResponse> {
	@Override
	public ObjectResponse execute(RequestType<ExtObject> crudRequest)
			throws ConnectorDataException {
		final ObjectResponse response = new ObjectResponse();
		response.setStatus(StatusCodeType.SUCCESS);
		ConnectorConfiguration config = getConfiguration(
				crudRequest.getTargetID(), ConnectorConfiguration.class);
		HttpURLConnection con = super.getConnection(config.getManagedSys());
		try {
			con.connect();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,"Could not connect");
		}
		con.disconnect();
		return response;
	}

	@Override
	protected String getCommandScriptHandler(String id) {
		// TODO Auto-generated method stub
		return null;
	}
}
