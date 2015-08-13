package org.openiam.connector.soap.command.user;

import java.net.HttpURLConnection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.common.data.ConnectorConfiguration;

import org.openiam.connector.soap.command.base.AbstractSoapCommand;
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
@Service("setPasswordSoapCommand")
public class SetPasswordSoapCommand extends
		AbstractSoapCommand<PasswordRequest, ResponseType> {
	private static final Log log = LogFactory
			.getLog(SetPasswordSoapCommand.class);

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

			// connection.setDoOutput(true);
			connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
			connection.setRequestProperty("If-Match", principalName);

			// token.setTimestamp(System.currentTimeMillis());
			// TODO check how to get original password
			// token.setPassword("foobar");
			// String encrypted =token.getPassword();
			// String encrypted = TestRSA.encrypt(token);
			// connection.setRequestProperty("Authorization", "Bearer "
			// + encrypted);
			
			Map<String, String> user = objectToAttributes(
					passwordRequest.getObjectIdentity(),
					passwordRequest.getExtensibleObject());
			String commandHandler = this
					.getCommandScriptHandler(passwordRequest.getTargetID());
			String scriptName = this.getScriptName(commandHandler);
			String argsName = this.getArgs(commandHandler, user);

			makeCall(
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
	protected String getCommandScriptHandler(String id){
		return "";
	}
}
