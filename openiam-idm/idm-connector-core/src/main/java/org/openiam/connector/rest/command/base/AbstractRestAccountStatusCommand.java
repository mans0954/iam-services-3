package org.openiam.connector.rest.command.base;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.SuspendResumeRequest;
import org.openiam.base.response.ResponseType;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/12/13 Time: 1:48 AM To
 * change this template use File | Settings | File Templates.
 */
public abstract class AbstractRestAccountStatusCommand extends
		AbstractRestCommand<SuspendResumeRequest, ResponseType> {

	protected enum AccountStatus {
		LOCKED("lock"), UNLOCKED("unlock");

		private String name;

		AccountStatus(final String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static final String SQL = "ALTER USER \"%s\" account %s";

	@Override
	public ResponseType execute(SuspendResumeRequest request)
			throws ConnectorDataException {
		final ResponseType response = new ResponseType();
		response.setStatus(StatusCodeType.SUCCESS);

		final String principalName = request.getObjectIdentity();
		ConnectorConfiguration config = getConfiguration(request.getTargetID(),
				ConnectorConfiguration.class);
		String resourceId = config.getResourceId();
		
		return response;
	}

	protected abstract String getNewAccountStatus();
}
