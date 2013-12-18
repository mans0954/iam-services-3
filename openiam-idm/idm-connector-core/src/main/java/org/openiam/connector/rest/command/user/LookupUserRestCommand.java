package org.openiam.connector.rest.command.user;

import java.net.HttpURLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openiam.connector.rest.command.base.AbstractSearchRestCommand;

import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/12/13 Time: 1:17 AM To
 * change this template use File | Settings | File Templates.
 */
@Service("lookupUserRestCommand")
public class LookupUserRestCommand extends
		AbstractSearchRestCommand<ExtensibleUser> {
	private static final Log log = LogFactory
			.getLog(LookupUserRestCommand.class);

	@Override
	protected String searchObject(HttpURLConnection connection, String dataId)
			throws Exception {
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
			return makeCall(connection, "");

		} finally {
			// this.closeStatement(statement);
		}
	}
}
