package org.openiam.connector.rest.command.user;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openiam.connector.rest.command.base.AbstractLookupRestCommand;
import org.openiam.connector.rest.command.base.AbstractSearchRestCommand;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;

import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/12/13 Time: 1:17 AM To
 * change this template use File | Settings | File Templates.
 */
@Service("lookupUserRestCommand")
public class LookupUserRestCommand extends
		AbstractLookupRestCommand<ExtensibleUser> {
	private static final Log log = LogFactory
			.getLog(LookupUserRestCommand.class);

	@Override
	protected String getCommandScriptHandler(String id) {
		return managedSysService.getManagedSysById(id).getLookupHandler();
	}

	@Override
	protected SearchResponse searchObject(HttpURLConnection con,
			LookupRequest<ExtensibleUser> searchRequest) throws Exception {
		try {
			con.setDoOutput(true);
			con.setRequestProperty("Accept", "application/xml");
			con.setRequestProperty("X-HTTP-Method-Override", "GET");
			
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
			//con.connect();
			//return makeCall(con, msg).getErrorMsgAsStr();
			ObjectResponse objectResponse = makeCall(con, "");
			SearchResponse response = new SearchResponse();
			response.setStatus(objectResponse.getStatus());
			return response;

		} finally {
			// this.closeStatement(statement);
		}
	}

}
