package org.openiam.connector.rest.command.user;

import java.net.HttpURLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openiam.connector.rest.command.base.AbstractDeleteRestCommand;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
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
	protected void deleteObject(String dataId, HttpURLConnection connection)
			throws ConnectorDataException {
		try {
			// connection.setDoOutput(true);
			// connection.setRequestProperty("X-HTTP-Method-Override",
			// "DELETE");

			// token.setTimestamp(System.currentTimeMillis());
			// TODO check how to get this

			// String encrypted =token.getPassword();
			// String encrypted = TestRSA.encrypt(token);
			// connection
			// .setRequestProperty(
			// "Authorization",
			// "Bearer " + encrypted);
			makeCall(connection, "");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
					e.getMessage());
		}
	}

}
