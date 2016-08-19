package org.openiam.connector.rest.command.user;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.common.data.ConnectorConfiguration;

import org.openiam.connector.rest.command.base.AbstractRestCommand;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.PasswordRequest;
import org.openiam.base.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.base.request.NotificationParam;
import org.openiam.base.request.NotificationRequest;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/12/13 Time: 1:33 AM To
 * change this template use File | Settings | File Templates.
 */
@Service("setPasswordRestCommand")
public class SetPasswordRestCommand extends
		AbstractRestCommand<PasswordRequest, ResponseType> {
	private static final Log log = LogFactory
			.getLog(SetPasswordRestCommand.class);

	@Override
	public ResponseType execute(PasswordRequest passwordRequest)
			throws ConnectorDataException {
		final ResponseType response = new ResponseType();
		response.setStatus(StatusCodeType.SUCCESS);

		final String principalName = passwordRequest.getObjectIdentity();
		ConnectorConfiguration config = getConfiguration(
				passwordRequest.getTargetID(), ConnectorConfiguration.class);
		ManagedSysEntity managedSys = config.getManagedSys();
		managedSys.setConnectionString(managedSys.getConnectionString() + "/" + principalName + "/password" );
		HttpURLConnection connection = getConnection(managedSys);
		
		try {

			connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
			connection.setRequestProperty("If-Match", principalName);

			Map<String, String> user = objectToAttributes(
					passwordRequest.getObjectIdentity(),
					passwordRequest.getExtensibleObject());
			String commandHandler = this
					.getCommandScriptHandler(passwordRequest.getTargetID());
			String scriptName = this.getScriptName(commandHandler);
			String argsName = this.getArgs(commandHandler, user);
			final NotificationRequest notificationRequest = new NotificationRequest();

			notificationRequest.getParamList()
					.add(new NotificationParam("PSWD", passwordRequest
							.getPassword()));
			Map<String, Object> bindingMap = new HashMap<String, Object>();
			bindingMap.put("req", notificationRequest);
			String msg = createMessage(bindingMap, scriptName);
			connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
			connection.setRequestProperty("If-Match", principalName);
			makeCall(connection, msg);

			return response;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new ConnectorDataException(ErrorCode.OTHER_ERROR,
					e.getMessage());
		} finally {
			connection.disconnect();
		}
	}

	protected String getCommandScriptHandler(String id) {
		return managedSysService.getManagedSysById(id).getPasswordHandler();
	}

}
