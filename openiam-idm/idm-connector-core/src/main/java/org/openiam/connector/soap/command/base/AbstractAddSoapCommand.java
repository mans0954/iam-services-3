package org.openiam.connector.soap.command.base;

import java.net.HttpURLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.CrudRequest;
import org.openiam.base.response.ObjectResponse;
import org.openiam.provision.type.ExtensibleObject;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/11/13 Time: 10:37 PM To
 * change this template use File | Settings | File Templates.
 */
public abstract class AbstractAddSoapCommand<ExtObject extends ExtensibleObject>
		extends AbstractSoapCommand<CrudRequest<ExtObject>, ObjectResponse> {
	private static final Log log = LogFactory
			.getLog(AbstractSoapCommand.class);

	@Override
	public ObjectResponse execute(CrudRequest<ExtObject> crudRequest)
			throws ConnectorDataException {
		final ObjectResponse response = new ObjectResponse();
		response.setStatus(StatusCodeType.SUCCESS);
      log.info("Inside Scim add Abstract TargetId="+crudRequest.getTargetID());
		ConnectorConfiguration config = getConfiguration(
				crudRequest.getTargetID(), ConnectorConfiguration.class);

		if (crudRequest.getObjectIdentity() == null)
			throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION,
					"No identity sent");

		 //final ExtObject extObject = crudRequest.getExtensibleObject();
		
		// if(log.isDebugEnabled()) {
		// log.debug(String.format("ExtensibleObject in Add Request=%s",
		// extObject));
		// }

		// final List<AttributeMapEntity> attributeMap =
		// attributeMaps(resourceId);
		log.info("Inside Rest add Abstract MangeSysId="+config.getManagedSys());
		HttpURLConnection con = this.getConnection(config.getManagedSys(),
				"/v1/Users");
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
