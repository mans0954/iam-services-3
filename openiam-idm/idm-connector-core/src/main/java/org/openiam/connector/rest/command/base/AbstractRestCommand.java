package org.openiam.connector.rest.command.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.script.ScriptIntegration;
import org.openiam.connector.common.command.AbstractCommand;
import org.openiam.connector.common.jdbc.AbstractJDBCCommand;
import org.openiam.connector.common.rest.RESTConnectionMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/11/13 Time: 10:23 PM To
 * change this template use File | Settings | File Templates.
 */
public abstract class AbstractRestCommand<Request extends RequestType, Response extends ResponseType>
		extends AbstractCommand<Request, Response> {
	private static final Log log = LogFactory.getLog(AbstractRestCommand.class);
	@Autowired
	@Qualifier("configurableGroovyScriptEngine")
	private ScriptIntegration scriptRunner;
	@Autowired
	@Qualifier("restConnection")
	protected RESTConnectionMgr connectionMgr;

	protected String getResourceId(String targetID, ManagedSysEntity managedSys)
			throws ConnectorDataException {
		if (managedSys == null)
			throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION,
					String.format("No Managed System with target id: %s",
							targetID));

		if (managedSys.getResource() == null || !StringUtils.hasText(managedSys.getResource().getId()))
			throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION,
					"ResourceID is not defined in the ManagedSys Object");
		return managedSys.getResource().getId();
	}

	protected String createMessage(Map<String, Object> bindingMap,
			String handlerScript) {
		try {
			return (String) scriptRunner.execute(bindingMap, handlerScript);
		} catch (Exception e) {
			log.error("createMessage():" + e.toString());
			return null;
		}
	}

	protected HttpURLConnection getConnection(ManagedSysEntity managedSys)
			throws ConnectorDataException {

		ManagedSysDto dto = managedSysDozerConverter.convertToDTO(managedSys,
				false);
		// dto.setDecryptPassword(this.getDecryptedPassword(managedSys.getPswd()));
		HttpURLConnection con = null;

		try {
			String scriptName = this.getScriptName(managedSys.getHostUrl());
			Map<String, Object> bindingMap = new HashMap<String, Object>();
			String encrypted = createMessage(bindingMap, scriptName);
			con = connectionMgr.connect(dto, encrypted); 
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
					"Cannot connect to to target system");
		}
		if (con == null)
			throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
					"Cannot connect to to target system");
		return con;
	}

	protected static ObjectResponse makeCall(HttpURLConnection connection,
			String input) throws IOException {
		final ObjectResponse response = new ObjectResponse();
		response.setStatus(StatusCodeType.FAILURE);
		OutputStreamWriter out = new OutputStreamWriter(
				connection.getOutputStream(), "UTF-8");
		out.write(input);
		out.flush();
		BufferedReader in = null;

		try {
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			response.setStatus(StatusCodeType.SUCCESS);
		} catch (java.io.IOException e) {
			response.setStatus(StatusCodeType.FAILURE);
			if (connection.getErrorStream() != null) {
				in = new BufferedReader(new InputStreamReader(
						connection.getErrorStream()));
			}
		}
		StringBuilder responseStr = new StringBuilder();

		if (in != null) {
			for (String s = in.readLine(); s != null; s = in.readLine()) {
				responseStr.append(s);
				log.info(s);
			}
			in.close();
		}
		out.close();
		log.info("HeaderFiles in asc=" + connection.getHeaderFields());
		response.addErrorMessage(responseStr.toString());
		return response;
	}

	protected abstract String getCommandScriptHandler(String id);

	protected String getScriptName(String commandHandler) {
		String name = "";
		if (StringUtils.hasText(commandHandler)) {
			String[] args = commandHandler.trim().split(" ");
			if (args != null) {
				if (args[0] != null)
					name = args[0];
			}
		} else {
			log.info("Handler not found");
		}
		return name;
	}

	protected String getArgs(String commandHandler, Map<String, String> user)
			throws Exception {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.hasText(commandHandler)) {
			String[] args = commandHandler.trim().split(" ");
			if (args != null && args.length > 1) {
				for (int i = 1; i < args.length; i++) {
					if (user.get(args[i]) == null) {
						sb.append("\"\"");
					} else {
						sb.append("\"");
						sb.append(user.get(args[i]));
						sb.append("\" ");
					}
					sb.append(" ");
				}
			}
		} else {
			log.info("Handler not found");
		}
		return sb.toString();
	}

}
