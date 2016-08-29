package org.openiam.connector.scim.command.user;

import java.net.HttpURLConnection;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.common.scim.S;
import org.openiam.connector.common.scim.TestRSA;
import org.openiam.connector.scim.command.base.AbstractModifyScimCommand;
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
@Service("modifyUserScimCommand")
public class ModifyUserScimCommand extends
		AbstractModifyScimCommand<ExtensibleUser> {
	private static final Log log = LogFactory
			.getLog(ModifyUserScimCommand.class);

	@Override
	protected void modifyObject(CrudRequest<ExtensibleUser> crudRequest,
			HttpURLConnection connection) throws ConnectorDataException {
		String identifiedBy = null;
		ExtensibleObject obj = crudRequest.getExtensibleObject();

		if (StringUtils.isNotBlank(crudRequest.getObjectIdentity())) {
			// Extract attribues into a map. Also save groups
			HashMap<String, String> attributes = new HashMap<String, String>();
			attributes.put("login", crudRequest.getObjectIdentity());
			if (obj == null) {
				if(log.isDebugEnabled()) {
					log.debug("Object: not provided, just identity, seems it is delete operation");
				}
			} else {
				if(log.isDebugEnabled()) {
					log.debug("Object:" + obj.getName() + " - operation="
							+ obj.getOperation());
				}

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
						"Number of attributes to persist in MODIFY = %s",
						attributes.size()));
			}

			if (StringUtils.isBlank(identifiedBy))
				throw new ConnectorDataException(ErrorCode.INVALID_ATTRIBUTE,
						"No password specified");

			try {

				connection.setDoOutput(true);
				connection
						.setRequestProperty("Content-Type", "application/xml");
				connection.setRequestProperty("Accept", "application/json");
				connection.setRequestProperty("X-HTTP-Method-Override", "PUT");
				connection.setRequestProperty("If-Match",
						crudRequest.getObjectIdentity());

				S token = new S();
				token.setTimestamp(System.currentTimeMillis());
				token.setPassword("foobar");
				// String encrypted =token.getPassword();
				String encrypted = TestRSA.encrypt(token);
				connection.setRequestProperty("Authorization", "Bearer "
						+ encrypted);

				connection.connect();

				makeCall(
						connection,
						"<User xmlns=\"urn:scim:schemas:core:1.0\" "
								+ "xmlns:enterprise=\"urn:scim:schemas:extension:enterprise:1.0\">"
								+ "<userName>"
								+ crudRequest.getObjectIdentity()
								+ "</userName>"
								+ "<preferredLanguage>en_US</preferredLanguage>"
								+ "<emails>"
								+ "<email>"
								+ "<value>a"
								+ crudRequest.getObjectIdentity()
								+ "u@test.com</value>"
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

}
