package org.openiam.connector.scim.command.base;

import java.net.HttpURLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.type.ObjectValue;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.SearchRequest;
import org.openiam.base.response.ObjectResponse;

import org.openiam.base.response.SearchResponse;
import org.openiam.provision.type.ExtensibleObject;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/12/13 Time: 1:00 AM To
 * change this template use File | Settings | File Templates.
 */
public abstract class AbstractSearchScimCommand<ExtObject extends ExtensibleObject>
		extends AbstractScimCommand<SearchRequest<ExtObject>, SearchResponse> {
	private static final Log log = LogFactory
			.getLog(AbstractSearchScimCommand.class);

	@Override
	public SearchResponse execute(SearchRequest<ExtObject> searchRequest)
			throws ConnectorDataException {
		final SearchResponse response = new SearchResponse();
		response.setStatus(StatusCodeType.SUCCESS);

		final String dataId = searchRequest.getSearchValue();
		/* targetID - */
		ConnectorConfiguration config = getConfiguration(
				searchRequest.getTargetID(), ConnectorConfiguration.class);
		HttpURLConnection con = this.getConnection(config.getManagedSys(),
				"/v1/Users/" + dataId);
		try {
			final ObjectValue resultObject = new ObjectValue();
			resultObject.setObjectIdentity(dataId);
			ObjectResponse objectResponse = searchObject(con, dataId);
			response.setStatus(objectResponse.getStatus());
			return response;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new ConnectorDataException(ErrorCode.OTHER_ERROR,
					e.getMessage());
		} finally {
			con.disconnect();
		}
	}

	protected abstract ObjectResponse searchObject(HttpURLConnection con,
			String dataId) throws Exception;
}
