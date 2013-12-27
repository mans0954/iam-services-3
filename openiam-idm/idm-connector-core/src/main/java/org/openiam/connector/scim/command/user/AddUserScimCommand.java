package org.openiam.connector.scim.command.user;

import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.common.scim.S;
import org.openiam.connector.common.scim.TestRSA;
import org.openiam.connector.scim.command.base.AbstractAddScimCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/11/13 Time: 10:22 PM To
 * change this template use File | Settings | File Templates.
 */
@Service("addUserScimCommand")
public class AddUserScimCommand extends AbstractAddScimCommand<ExtensibleUser> {
	private static final Log log = LogFactory.getLog(AddUserScimCommand.class);

	@Override
	protected void addObject(CrudRequest<ExtensibleUser> crudRequest,
			HttpURLConnection connection) throws ConnectorDataException {

		log.info("Inside AddUserScimCommand");

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

				S token = new S();
				token.setTimestamp(System.currentTimeMillis());
				String encrypted = TestRSA.encrypt(token);

				long nanoTime = System.nanoTime();
				connection.connect();

				makeCall(
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

}
