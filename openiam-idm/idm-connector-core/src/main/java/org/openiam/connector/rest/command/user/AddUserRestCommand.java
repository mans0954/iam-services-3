package org.openiam.connector.rest.command.user;

import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openiam.connector.rest.command.base.AbstractAddRestCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/11/13 Time: 10:22 PM To
 * change this template use File | Settings | File Templates.
 */
@Service("addUserRestCommand")
public class AddUserRestCommand extends AbstractAddRestCommand<ExtensibleUser> {
	private static final Log log = LogFactory.getLog(AddUserRestCommand.class);

	@Override
	protected void addObject(CrudRequest<ExtensibleUser> crudRequest,
			HttpURLConnection connection) throws ConnectorDataException {

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
				
				long nanoTime = System.nanoTime();
				Map<String, String> user = objectToAttributes(
						crudRequest.getObjectIdentity(),
						crudRequest.getExtensibleObject());
				String commandHandler = this
						.getCommandScriptHandler(crudRequest.getTargetID());
				String scriptName = this.getScriptName(commandHandler);
				String argsName = this.getArgs(commandHandler, user);
				final NotificationRequest notificationRequest = new NotificationRequest();
				notificationRequest.getParamList().add(
						new NotificationParam("IDENTITY", crudRequest
								.getObjectIdentity()));
				notificationRequest.getParamList().add(
						new NotificationParam("PSWD", identifiedBy));
				Map<String, Object> bindingMap = new HashMap<String, Object>();
				bindingMap.put("req", notificationRequest);
				String msg = createMessage(bindingMap, scriptName);
				makeCall(
						connection,
						msg);

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
