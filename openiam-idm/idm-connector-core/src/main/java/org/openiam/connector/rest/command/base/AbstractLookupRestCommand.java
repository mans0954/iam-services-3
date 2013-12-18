package org.openiam.connector.rest.command.base;

import java.net.HttpURLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.provision.type.ExtensibleObject;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/12/13 Time: 1:00 AM To
 * change this template use File | Settings | File Templates.
 */
public abstract class AbstractLookupRestCommand<ExtObject extends ExtensibleObject>
		extends AbstractRestCommand<LookupRequest<ExtObject>, SearchResponse> {
	private static final Log log = LogFactory
			.getLog(AbstractLookupRestCommand.class);

	@Override
	public SearchResponse execute(LookupRequest<ExtObject> searchRequest)
			throws ConnectorDataException {
		final SearchResponse response = new SearchResponse();
		response.setStatus(StatusCodeType.SUCCESS);

		final String dataId = searchRequest.getSearchValue();
		/* targetID - */
		log.info("Inside Abstract LookUp TargetId="+searchRequest.getTargetID());
		ConnectorConfiguration config = getConfiguration(
				searchRequest.getTargetID(), ConnectorConfiguration.class);
		log.info("Inside Abstract LookUp ManageSysId="+config.getManagedSys());
		HttpURLConnection con = this.getConnection(config.getManagedSys(),
				"Users/" + dataId);
		try {
			final ObjectValue resultObject = new ObjectValue();
			resultObject.setObjectIdentity(dataId);
			final String responseStr = searchObject(con, dataId);

			if (log.isDebugEnabled()) {
				log.info(String.format("Response= %s", responseStr));
			}

			if (responseStr != null) {
				// response.getObjectList().add(resultObject);
				response.setStatus(StatusCodeType.SUCCESS);
			} else {
				response.setStatus(StatusCodeType.FAILURE);
				log.info("LOOKUP successful without results.");
				// throw new
				// ConnectorDataException(ErrorCode.NO_RESULTS_RETURNED);
			}
			// else
			// throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
			// "Principal not found");
            
			return response;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new ConnectorDataException(ErrorCode.OTHER_ERROR,
					e.getMessage());
		} finally {
			con.disconnect();
		}
	}

	protected abstract String searchObject(HttpURLConnection con, String dataId)
			throws Exception;
}
