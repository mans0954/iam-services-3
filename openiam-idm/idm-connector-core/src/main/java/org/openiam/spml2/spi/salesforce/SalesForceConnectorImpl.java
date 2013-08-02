package org.openiam.spml2.spi.salesforce;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.type.*;
import org.openiam.connector.type.ResponseType;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.connector.ConnectorService;
import org.openiam.spml2.spi.common.AddCommand;
import org.openiam.spml2.spi.common.DeleteCommand;
import org.openiam.spml2.spi.common.LookupCommand;
import org.openiam.spml2.spi.common.ModifyCommand;
import org.openiam.spml2.spi.common.PasswordCommand;
import org.openiam.spml2.spi.common.ResumeCommand;
import org.openiam.spml2.spi.common.SuspendCommand;
import org.openiam.connector.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Required;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

@WebService(endpointInterface="org.openiam.connector.ConnectorService",
	targetNamespace="http://www.openiam.org/service/connector",
	portName = "SalesForceServicePort", 
	serviceName="SalesForceConnectorService")
public class SalesForceConnectorImpl implements ConnectorService {
	
	private static final Log log = LogFactory.getLog(SalesForceConnectorImpl.class);
	
    private AddCommand addCommand;
    private DeleteCommand deleteCommand;
    private LookupCommand lookupCommand;
    private ModifyCommand modifyCommand;
    private ResumeCommand resumeCommand;
    private PasswordCommand setPasswordCommand;
    private SuspendCommand suspendCommand;

	@Override
	@WebMethod
	public ResponseType reconcileResource(@WebParam(name = "config", targetNamespace = "") ReconciliationConfig config) {
        final ResponseType response = new ResponseType();
        
        response.setStatus(StatusCodeType.FAILURE);
        response.setError(ErrorCode.UNSUPPORTED_OPERATION);
        return response;
	}

	@Override
	@WebMethod
	public ResponseType testConnection(@WebParam(name = "managedSys", targetNamespace = "") ManagedSysDto managedSys) {
		final ResponseType response = new ResponseType();
		response.setStatus(StatusCodeType.SUCCESS);
		try {
			final ConnectorConfig connectorConfig = new ConnectorConfig();
			connectorConfig.setUsername(managedSys.getUserId());
			connectorConfig.setPassword(managedSys.getDecryptPassword());
			connectorConfig.setAuthEndpoint(managedSys.getConnectionString());
			final PartnerConnection partnerConnection = new PartnerConnection(connectorConfig);
		} catch (ConnectionException e) {
			log.error("Connection Exception", e);
			ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.AUTHENTICATION_FAILED, e.getMessage());
		}
		return response;
	}

    public UserResponse add(UserRequest reqType) {
        return addCommand.add(reqType);
    }

    public UserResponse modify(UserRequest reqType) {
        return modifyCommand.modify(reqType);
    }

    public UserResponse delete(UserRequest reqType) {
        return deleteCommand.delete(reqType);
    }

    public SearchResponse lookup( LookupRequest reqType) {
        return lookupCommand.lookup(reqType);
    }

    /*
* (non-Javadoc)
*
* @see org.openiam.spml2.interf.SpmlCore#lookupAttributeNames(org.openiam.spml2.msg.
* LookupAttributeRequestType)
*/
    public LookupAttributeResponse lookupAttributeNames(LookupRequest reqType){
        LookupAttributeResponse respType = new LookupAttributeResponse();
        respType.setStatus(StatusCodeType.FAILURE);
        respType.setError(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION);

        return respType;
    }

    public ResponseType setPassword( PasswordRequest request) {
        return setPasswordCommand.setPassword(request);
    }

    public ResponseType expirePassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResponseType resetPassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResponseType validatePassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SearchResponse search(@WebParam(name = "searchRequest", targetNamespace = "") SearchRequest searchRequest) {
        throw new UnsupportedOperationException("Not supportable.");
    }

    public ResponseType suspend(final SuspendRequest request) {
        return suspendCommand.suspend(request);
    }

    public ResponseType resume(final ResumeRequest request) {
        return resumeCommand.resume(request);
    }

    @Required
    public void setAddCommand(final AddCommand addCommand) {
        this.addCommand = addCommand;
    }

    @Required
    public void setDeleteCommand(final DeleteCommand deleteCommand) {
        this.deleteCommand = deleteCommand;
    }

    @Required
    public void setLookupCommand(final LookupCommand lookupCommand) {
        this.lookupCommand = lookupCommand;
    }

    @Required
    public void setModifyCommand(final ModifyCommand modifyCommand) {
        this.modifyCommand = modifyCommand;
    }

    @Required
    public void setResumeCommand(final ResumeCommand resumeCommand) {
        this.resumeCommand = resumeCommand;
    }

    @Required
    public void setSetPasswordCommand(final PasswordCommand setPasswordCommand) {
        this.setPasswordCommand = setPasswordCommand;
    }

    @Required
    public void setSuspendCommand(final SuspendCommand suspendCommand) {
        this.suspendCommand = suspendCommand;
    }
}
