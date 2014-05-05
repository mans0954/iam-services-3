package org.openiam.connector.rest.command.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;

import org.openiam.provision.type.ExtensibleObject;

import java.net.HttpURLConnection;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/11/13 Time: 10:37 PM To
 * change this template use File | Settings | File Templates.
 */
public abstract class AbstractModifyRestCommand<ExtObject extends ExtensibleObject>
		extends AbstractRestCommand<CrudRequest<ExtObject>, ObjectResponse> {
	private static final Log log = LogFactory
			.getLog(AbstractModifyRestCommand.class);

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
		ManagedSysEntity managedSys = config.getManagedSys();
		managedSys.setConnectionString(managedSys.getConnectionString() + "/" + crudRequest.getObjectIdentity());

		HttpURLConnection con = this.getConnection(managedSys);
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
