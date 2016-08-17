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
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/12/13
 * Time: 12:27 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDeleteSoapCommand<ExtObject extends ExtensibleObject> extends AbstractSoapCommand<CrudRequest<ExtObject>, ObjectResponse> {
	private static final Log log = LogFactory
	.getLog(AbstractDeleteSoapCommand.class);
	
    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> deleteRequestType) throws ConnectorDataException {
        final ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        final String dataId = deleteRequestType.getObjectIdentity();
        ConnectorConfiguration config =  getConfiguration(deleteRequestType.getTargetID(), ConnectorConfiguration.class);
        HttpURLConnection con = getConnection(config.getManagedSys(), "/v1/Users/" + dataId);
        try {
            deleteObject(deleteRequestType, con);
            return response;
        }  catch(Throwable e) {
            log.error(e.getMessage(),e);
            throw  new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
        } finally {
           con.disconnect();
        }
    }

    protected abstract void deleteObject(CrudRequest<ExtObject> deleteRequestType,  HttpURLConnection con)throws ConnectorDataException;
}
