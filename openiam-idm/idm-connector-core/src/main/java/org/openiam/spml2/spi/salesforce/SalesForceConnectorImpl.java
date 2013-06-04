package org.openiam.spml2.spi.salesforce;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.spml2.interf.ConnectorService;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.msg.password.ExpirePasswordRequestType;
import org.openiam.spml2.msg.password.ResetPasswordRequestType;
import org.openiam.spml2.msg.password.ResetPasswordResponseType;
import org.openiam.spml2.msg.password.SetPasswordRequestType;
import org.openiam.spml2.msg.password.ValidatePasswordRequestType;
import org.openiam.spml2.msg.password.ValidatePasswordResponseType;
import org.openiam.spml2.msg.suspend.ResumeRequestType;
import org.openiam.spml2.msg.suspend.SuspendRequestType;
import org.openiam.spml2.spi.common.AddCommand;
import org.openiam.spml2.spi.common.DeleteCommand;
import org.openiam.spml2.spi.common.LookupCommand;
import org.openiam.spml2.spi.common.ModifyCommand;
import org.openiam.spml2.spi.common.PasswordCommand;
import org.openiam.spml2.spi.common.ResumeCommand;
import org.openiam.spml2.spi.common.SuspendCommand;
import org.openiam.spml2.util.msg.ResponseBuilder;
import org.springframework.beans.factory.annotation.Required;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

@WebService(endpointInterface="org.openiam.spml2.interf.ConnectorService",
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

    public AddResponseType add(AddRequestType reqType) {
        return addCommand.add(reqType);
    }

    public ModifyResponseType modify(ModifyRequestType reqType) {
        return modifyCommand.modify(reqType);
    }

    public ResponseType delete(DeleteRequestType reqType) {

        return deleteCommand.delete(reqType);
    }

    public LookupResponseType lookup( LookupRequestType reqType) {
        return lookupCommand.lookup(reqType);
    }

    /*
* (non-Javadoc)
*
* @see org.openiam.spml2.interf.SpmlCore#lookupAttributeNames(org.openiam.spml2.msg.
* LookupAttributeRequestType)
*/
    public LookupAttributeResponseType lookupAttributeNames(LookupAttributeRequestType reqType){
        LookupAttributeResponseType respType = new LookupAttributeResponseType();
        respType.setStatus(StatusCodeType.FAILURE);
        respType.setError(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION);

        return respType;
    }

    public ResponseType setPassword( SetPasswordRequestType request) {
        return setPasswordCommand.setPassword(request);
    }

    public ResponseType expirePassword(@WebParam(name = "request", targetNamespace = "") ExpirePasswordRequestType request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResetPasswordResponseType resetPassword(@WebParam(name = "request", targetNamespace = "") ResetPasswordRequestType request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ValidatePasswordResponseType validatePassword(@WebParam(name = "request", targetNamespace = "") ValidatePasswordRequestType request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResponseType suspend(final SuspendRequestType request) {
        return suspendCommand.suspend(request);
    }

    public ResponseType resume(final ResumeRequestType request) {
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
