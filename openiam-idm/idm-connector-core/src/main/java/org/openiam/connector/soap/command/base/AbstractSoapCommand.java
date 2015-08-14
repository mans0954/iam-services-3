package org.openiam.connector.soap.command.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Map;

import org.springframework.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.connector.common.command.AbstractCommand;
import org.openiam.connector.common.jdbc.AbstractJDBCCommand;

import org.openiam.connector.common.soap.SOAPConnectionMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/11/13 Time: 10:23 PM To
 * change this template use File | Settings | File Templates.
 */
public abstract class AbstractSoapCommand<Request extends RequestType, Response extends ResponseType>
		extends AbstractCommand<Request, Response> {
	private static final Log log = LogFactory.getLog(AbstractSoapCommand.class);

	@Autowired
	@Qualifier("soapConnection")
	protected SOAPConnectionMgr connectionMgr;

	protected String getResourceId(String targetID, ManagedSysEntity managedSys)
			throws ConnectorDataException {
		if (managedSys == null)
			throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION,
					String.format("No Managed System with target id: %s",
							targetID));

		if (managedSys.getResource() == null || StringUtils.hasText(managedSys.getResource().getId()))
			throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION,
					"ResourceID is not defined in the ManagedSys Object");
		return managedSys.getResource().getId();
	}

	protected HttpURLConnection getConnection(ManagedSysEntity managedSys,
			String appendToUrl) throws ConnectorDataException {
		log.info("inside GetConnecton url=" + appendToUrl);
		ManagedSysDto dto = managedSysDozerConverter.convertToDTO(managedSys,
				false);
		// dto.setDecryptPassword(this.getDecryptedPassword(managedSys.getPswd()));
		HttpURLConnection con = null;

		try {
			con = connectionMgr.connect(dto, appendToUrl);
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

	protected static String makeCall(HttpURLConnection connection, String input)
			throws IOException {
		OutputStreamWriter out = new OutputStreamWriter(
				connection.getOutputStream(), "UTF-8");
		out.write(input);
		out.flush();
		BufferedReader in = null;

		try {
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
		} catch (java.io.IOException e) {

			if (connection.getErrorStream() != null) {
				in = new BufferedReader(new InputStreamReader(
						connection.getErrorStream()));
			}
		}
		StringBuilder response = new StringBuilder();

		if (in != null) {
			for (String s = in.readLine(); s != null; s = in.readLine()) {
				response.append(s);
				log.info(s);
			}
			in.close();
		}
		out.close();
		log.info(connection.getHeaderFields());
		return response.toString();
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
			log.info("Handler not founded");
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
			log.info("Handler not founded");
		}
		return sb.toString();
	}

}
