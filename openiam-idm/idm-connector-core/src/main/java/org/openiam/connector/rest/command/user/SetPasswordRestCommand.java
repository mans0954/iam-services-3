package org.openiam.connector.rest.command.user;

import java.net.HttpURLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.common.data.ConnectorConfiguration;

import org.openiam.connector.rest.command.base.AbstractRestCommand;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.response.ResponseType;
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

		HttpURLConnection connection = this.getConnection(
				config.getManagedSys(), "Users/" + principalName + "/password");
		try {

//			connection.setDoOutput(true);
			connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
			connection.setRequestProperty("If-Match", principalName);

	
//			token.setTimestamp(System.currentTimeMillis());
			// TODO check how to get original password
//			token.setPassword("foobar");
//		    String encrypted =token.getPassword();
			//String encrypted = TestRSA.encrypt(token);
			// connection.setRequestProperty("Authorization", "Bearer "
			// + encrypted);

			super.makeCall(
					connection,
					"<User xmlns=\"urn:scim:schemas:core:1.0\" "
							+ "xmlns:enterprise=\"urn:scim:schemas:extension:enterprise:1.0\">"
							+ "<password>" + passwordRequest.getPassword()
							+ "</password>" + "</User>");

			return response;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new ConnectorDataException(ErrorCode.OTHER_ERROR,
					e.getMessage());
		} finally {
			connection.disconnect();
		}
	}
}
