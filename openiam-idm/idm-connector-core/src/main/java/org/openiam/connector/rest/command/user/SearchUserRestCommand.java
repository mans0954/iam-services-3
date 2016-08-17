package org.openiam.connector.rest.command.user;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openiam.connector.rest.command.base.AbstractSearchRestCommand;
import org.openiam.provision.request.SearchRequest;
import org.openiam.base.response.ObjectResponse;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/12/13 Time: 1:17 AM To
 * change this template use File | Settings | File Templates.
 */
@Service("searchUserRestCommand")
public class SearchUserRestCommand extends
		AbstractSearchRestCommand<ExtensibleUser> {
	private static final Log log = LogFactory
			.getLog(SearchUserRestCommand.class);

	@Override
	protected ObjectResponse searchObject(HttpURLConnection connection,
			SearchRequest<ExtensibleUser> searchRequest) throws Exception {

			connection.setRequestProperty("Accept", "application/xml");
			connection.setRequestProperty("X-HTTP-Method-Override", "GET");
			Map<String, String> user = objectToAttributes(
					searchRequest.getObjectIdentity(),
					searchRequest.getExtensibleObject());
			String commandHandler = this.getCommandScriptHandler(searchRequest
					.getTargetID());
			String scriptName = this.getScriptName(commandHandler);
			String argsName = this.getArgs(commandHandler, user);
			final NotificationRequest notificationRequest = new NotificationRequest();
			notificationRequest.getParamList().add(
					new NotificationParam("IDENTITY", searchRequest
							.getObjectIdentity()));

			Map<String, Object> bindingMap = new HashMap<String, Object>();
			bindingMap.put("req", notificationRequest);
			String msg = createMessage(bindingMap, scriptName);
			return makeCall(connection, msg);

		
	}

	@Override
	protected String getCommandScriptHandler(String id) {
		return managedSysService.getManagedSysById(id).getSearchHandler();
	}
}
