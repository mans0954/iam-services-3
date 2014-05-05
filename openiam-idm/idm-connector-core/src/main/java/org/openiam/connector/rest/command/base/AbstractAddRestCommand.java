package org.openiam.connector.rest.command.base;

import java.net.HttpURLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.provision.type.ExtensibleObject;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/11/13 Time: 10:37 PM To
 * change this template use File | Settings | File Templates.
 */
public abstract class AbstractAddRestCommand<ExtObject extends ExtensibleObject>
		extends AbstractRestCommand<CrudRequest<ExtObject>, ObjectResponse> {
	private static final Log log = LogFactory.getLog(AbstractRestCommand.class);

	@Override
	public ObjectResponse execute(CrudRequest<ExtObject> crudRequest)
			throws ConnectorDataException {
		final ObjectResponse response = new ObjectResponse();
		response.setStatus(StatusCodeType.SUCCESS);
		log.info("Inside Rest add Abstract TargetId="
				+ crudRequest.getTargetID());
		ConnectorConfiguration config = getConfiguration(
				crudRequest.getTargetID(), ConnectorConfiguration.class);

		if (crudRequest.getObjectIdentity() == null)
			throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION,
					"No identity sent");

		HttpURLConnection con = this.getConnection(config.getManagedSys());
		try {
			addObject(crudRequest, con);
			return response;
		} catch (ConnectorDataException e) {
			log.error(e.getMessage(), e);
			throw e;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new ConnectorDataException(ErrorCode.OTHER_ERROR,
					e.getMessage());
		} finally {
			con.disconnect();
		}
	}

	protected abstract void addObject(CrudRequest<ExtObject> crudRequest,
			HttpURLConnection con) throws ConnectorDataException;
}
