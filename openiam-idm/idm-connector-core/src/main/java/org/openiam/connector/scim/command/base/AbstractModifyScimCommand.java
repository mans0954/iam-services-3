package org.openiam.connector.scim.command.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.CrudRequest;
import org.openiam.base.response.ObjectResponse;
import org.openiam.provision.type.ExtensibleObject;
import java.net.HttpURLConnection;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/11/13 Time: 10:37 PM To
 * change this template use File | Settings | File Templates.
 */
public abstract class AbstractModifyScimCommand<ExtObject extends ExtensibleObject>
		extends AbstractScimCommand<CrudRequest<ExtObject>, ObjectResponse> {
	private static final Log log = LogFactory
			.getLog(AbstractModifyScimCommand.class);

	@Override
	public ObjectResponse execute(CrudRequest<ExtObject> crudRequest)
			throws ConnectorDataException {
		final ObjectResponse response = new ObjectResponse();
		response.setStatus(StatusCodeType.SUCCESS);

		ConnectorConfiguration config = getConfiguration(
				crudRequest.getTargetID(), ConnectorConfiguration.class);
		String resourceId = config.getResourceId();

		if (crudRequest.getObjectIdentity() == null)
			throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION,
					"No identity sent");

		// final ExtObject extObject = crudRequest.getExtensibleObject();
		//
		// if(log.isDebugEnabled()) {
		// log.debug(String.format("ExtensibleObject in Modify Request=%s",
		// extObject));
		// }

		HttpURLConnection con = this.getConnection(config.getManagedSys(),
				"/v1/Users/" + crudRequest.getObjectIdentity());
		try {
			modifyObject(crudRequest, con);
			// TODO check how to handle attribute map
			// modifyObject(crudRequest,attributeMap, con);
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

	protected abstract void modifyObject(CrudRequest<ExtObject> crudRequest,
			HttpURLConnection con) throws ConnectorDataException;
}
