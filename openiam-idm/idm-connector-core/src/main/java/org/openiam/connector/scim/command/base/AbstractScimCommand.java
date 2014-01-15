package org.openiam.connector.scim.command.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.connector.common.command.AbstractCommand;
import org.openiam.connector.common.jdbc.AbstractJDBCCommand;
import org.openiam.connector.common.scim.SCIMConnectionMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/11/13 Time: 10:23 PM To
 * change this template use File | Settings | File Templates.
 */
public abstract class AbstractScimCommand<Request extends RequestType, Response extends ResponseType>
		extends AbstractCommand<Request, Response> {
	private static final Log log = LogFactory.getLog(AbstractScimCommand.class);

	@Autowired
	@Qualifier("scimConnection")
	protected SCIMConnectionMgr connectionMgr;
	protected String getResourceId(String targetID, ManagedSysEntity managedSys)
			throws ConnectorDataException {
		if (managedSys == null)
			throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION,
					String.format("No Managed System with target id: %s",
							targetID));

		if (StringUtils.isBlank(managedSys.getResourceId()))
			throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION,
					"ResourceID is not defined in the ManagedSys Object");
		final Resource res = resourceDataService.getResource(managedSys
				.getResourceId());
		if (res == null)
			throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION,
					"No resource for managed resource found");

		return managedSys.getResourceId();
	}

	protected HttpURLConnection getConnection(ManagedSysEntity managedSys,
			String appendToUrl) throws ConnectorDataException {
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

	protected static ObjectResponse makeCall(HttpURLConnection connection,
			String input) throws UnsupportedEncodingException, IOException {
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

}
