package org.openiam.connector.rest.command.base;

import java.net.HttpURLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SearchRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleObject;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/12/13 Time: 1:00 AM To
 * change this template use File | Settings | File Templates.
 */
public abstract class AbstractSearchRestCommand<ExtObject extends ExtensibleObject>
		extends AbstractRestCommand<SearchRequest<ExtObject>, SearchResponse> {
	private static final Log log = LogFactory
			.getLog(AbstractSearchRestCommand.class);

	@Override
	public SearchResponse execute(SearchRequest<ExtObject> searchRequest)
			throws ConnectorDataException {
		final SearchResponse response = new SearchResponse();
		response.setStatus(StatusCodeType.SUCCESS);

		final String dataId = searchRequest.getSearchValue();
		/* targetID - */
		ConnectorConfiguration config = getConfiguration(
				searchRequest.getTargetID(), ConnectorConfiguration.class);
		ManagedSysEntity managedSys = config.getManagedSys();
		managedSys.setConnectionString(managedSys.getConnectionString() + "/" + dataId);
		HttpURLConnection con = getConnection(managedSys);
		try {
			final ObjectValue resultObject = new ObjectValue();
			resultObject.setObjectIdentity(dataId);
			
			ObjectResponse objectResponse = searchObject(con, searchRequest);
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

	protected abstract ObjectResponse searchObject(HttpURLConnection con, SearchRequest<ExtObject> searchRequest)
			throws Exception;
}
