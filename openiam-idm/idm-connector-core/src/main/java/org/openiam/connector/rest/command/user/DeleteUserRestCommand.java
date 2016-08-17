package org.openiam.connector.rest.command.user;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openiam.connector.rest.command.base.AbstractDeleteRestCommand;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.request.CrudRequest;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/12/13 Time: 12:35 AM To
 * change this template use File | Settings | File Templates.
 */
@Service("deleteUserRestCommand")
public class DeleteUserRestCommand extends
		AbstractDeleteRestCommand<ExtensibleUser> {
	private static final Log log = LogFactory
			.getLog(DeleteUserRestCommand.class);

	@Override
	protected void deleteObject(CrudRequest<ExtensibleUser> deleteRequestType,
			HttpURLConnection connection) throws ConnectorDataException {
		try {

			Map<String, String> user = objectToAttributes(
					deleteRequestType.getObjectIdentity(),
					deleteRequestType.getExtensibleObject());
			String commandHandler = this
					.getCommandScriptHandler(deleteRequestType.getTargetID());
			String scriptName = this.getScriptName(commandHandler);
			String argsName = this.getArgs(commandHandler, user);

			final NotificationRequest notificationRequest = new NotificationRequest();
			notificationRequest.getParamList().add(
					new NotificationParam("IDENTITY", deleteRequestType
							.getObjectIdentity()));

			Map<String, Object> bindingMap = new HashMap<String, Object>();
			bindingMap.put("req", notificationRequest);
			String msg = createMessage(bindingMap, scriptName);
    		connection.setDoOutput(true);
    		connection.setRequestProperty("X-HTTP-Method-Override", "DELETE");

			makeCall(connection, msg);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
					e.getMessage());
		}
	}

	@Override
	protected String getCommandScriptHandler(String id) {
		return managedSysService.getManagedSysById(id).getDeleteHandler();
	}

}
