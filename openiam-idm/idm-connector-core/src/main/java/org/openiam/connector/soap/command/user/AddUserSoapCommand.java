package org.openiam.connector.soap.command.user;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openiam.connector.soap.command.base.AbstractAddSoapCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.request.CrudRequest;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/11/13 Time: 10:22 PM To
 * change this template use File | Settings | File Templates.
 */
@Service("addUserSoapCommand")
public class AddUserSoapCommand extends AbstractAddSoapCommand<ExtensibleUser> {
	private static final Log log = LogFactory.getLog(AddUserSoapCommand.class);

	@Override
	protected void addObject(CrudRequest<ExtensibleUser> crudRequest,
			HttpURLConnection connection) throws ConnectorDataException {

		log.info("Inside AddUserSciamCommand");

		String identifiedBy = null;
		ExtensibleObject obj = crudRequest.getExtensibleObject();

		if (StringUtils.isNotBlank(crudRequest.getObjectIdentity())) {
			// Extract attribues into a map. Also save groups
			HashMap<String, String> attributes = new HashMap<String, String>();
			attributes.put("login", crudRequest.getObjectIdentity());
			if (obj == null) {

				log.info("Object: not provided, just identity, seems it is delete operation");
			} else {
				log.info("Object:" + obj.getName() + " - operation="
						+ obj.getOperation());

				// Extract attributes
				for (ExtensibleAttribute att : obj.getAttributes()) {
					if (att != null) {
						attributes.put(att.getName(), att.getValue());
					}
				}
			}

			identifiedBy = attributes.get("password");

			if (log.isDebugEnabled()) {
				log.debug(String.format(
						"Number of attributes to persist in ADD = %s",
						attributes.size()));
			}

			if (StringUtils.isBlank(identifiedBy))
				throw new ConnectorDataException(ErrorCode.INVALID_ATTRIBUTE,
						"No password specified");

			try {
				// connection.setDoOutput(true);
				// connection
				// .setRequestProperty("Content-Type", "application/xml");
				// connection.setRequestProperty("Accept", "application/json");

				// token.setTimestamp(System.currentTimeMillis());
				// String encrypted =token.getPassword();
				// String encrypted = TestRSA.encrypt(token);

				// connection.setRequestProperty("Authorization", "Bearer "
				// + encrypted);

				long nanoTime = System.nanoTime();
				
				Map<String, String> user = objectToAttributes(
						crudRequest.getObjectIdentity(),
						crudRequest.getExtensibleObject());
				String commandHandler = this
						.getCommandScriptHandler(crudRequest.getTargetID());
				String scriptName = this.getScriptName(commandHandler);
				String argsName = this.getArgs(commandHandler, user);

				String response = makeCall(
						connection,
						"<User xmlns=\"urn:scim:schemas:core:1.0\" "
								+ "xmlns:enterprise=\"urn:scim:schemas:extension:enterprise:1.0\">"
								+ "<userName>"
								+ crudRequest.getObjectIdentity()
								+ "</userName>"
								+ "<password>"
								+ identifiedBy
								+ "</password>"
								+ "<preferredLanguage>en_US</preferredLanguage>"
								+ "<emails>"
								+ "<email>"
								+ "<value>a"
								+ crudRequest.getObjectIdentity()
								+ "@test.com</value>"
								+ "<primary>true</primary>"
								+ "</email>"
								+ "</emails>"
								+ "<addresses><address><country>FI</country></address></addresses>"
								+ "<enterprise:gender>male</enterprise:gender>"
								+ "</User>");

			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
						e.getMessage());
			}
		}
	}
	 @Override
	    protected String getCommandScriptHandler(String mSysId) {
	        return managedSysService.getManagedSysById(mSysId).getAddHandler();
	    }
}
