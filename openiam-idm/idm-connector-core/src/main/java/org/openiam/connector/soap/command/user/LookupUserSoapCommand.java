package org.openiam.connector.soap.command.user;

import java.net.HttpURLConnection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openiam.connector.soap.command.base.AbstractSearchSoapCommand;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.request.SearchRequest;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/12/13 Time: 1:17 AM To
 * change this template use File | Settings | File Templates.
 */
@Service("lookupUserSoapCommand")
public class LookupUserSoapCommand extends
		AbstractSearchSoapCommand<ExtensibleUser> {
	private static final Log log = LogFactory
			.getLog(LookupUserSoapCommand.class);

	
	protected String getCommandScriptHandler(String id){
		return "";
	}

	@Override
	protected String searchObject(HttpURLConnection con,
			SearchRequest<ExtensibleUser> searchRequest) throws Exception {
		try {
			// connection.setDoOutput(true);
			// connection.setRequestProperty("Accept", "application/xml");
			// connection.setRequestProperty("X-HTTP-Method-Override", "GET");
			// S token = new S();
			// token.setTimestamp(System.currentTimeMillis());
			// token.setPassword("foobar");
			// String encrypted =token.getPassword();
			// //String encrypted = TestRSA.encrypt(token);
			// connection
			// .setRequestProperty(
			// "Authorization",
			// "Bearer " + encrypted);
			
			Map<String, String> user = objectToAttributes(
					searchRequest.getObjectIdentity(),
					searchRequest.getExtensibleObject());
			String commandHandler = this
					.getCommandScriptHandler(searchRequest.getTargetID());
			String scriptName = this.getScriptName(commandHandler);
			String argsName = this.getArgs(commandHandler, user);
			return makeCall(con, "");

		} finally {
			// this.closeStatement(statement);
		}
	}

}
