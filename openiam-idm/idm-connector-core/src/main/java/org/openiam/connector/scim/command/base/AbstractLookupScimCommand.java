package org.openiam.connector.scim.command.base;

import java.net.HttpURLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.provision.type.ExtensibleObject;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/12/13 Time: 1:00 AM To
 * change this template use File | Settings | File Templates.
 */
public abstract class AbstractLookupScimCommand<ExtObject extends ExtensibleObject>
		extends AbstractScimCommand<LookupRequest<ExtObject>, SearchResponse> {
	private static final Log log = LogFactory
			.getLog(AbstractLookupScimCommand.class);

	@Override
	public SearchResponse execute(LookupRequest<ExtObject> searchRequest)
			throws ConnectorDataException {

		final String dataId = searchRequest.getSearchValue();
		/* targetID - */
		ConnectorConfiguration config = getConfiguration(
				searchRequest.getTargetID(), ConnectorConfiguration.class);
		HttpURLConnection con = this.getConnection(config.getManagedSys(),
				"/v1/Users/" + dataId);
		try {
			final ObjectValue resultObject = new ObjectValue();
			resultObject.setObjectIdentity(dataId);

			return lookUpObject(con, dataId);

		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new ConnectorDataException(ErrorCode.OTHER_ERROR,
					e.getMessage());
		} finally {
			con.disconnect();
		}
	}

	protected abstract SearchResponse lookUpObject(HttpURLConnection con,
			String dataId) throws Exception;
}
