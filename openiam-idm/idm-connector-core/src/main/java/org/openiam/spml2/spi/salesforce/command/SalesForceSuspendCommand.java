package org.openiam.spml2.spi.salesforce;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.type.ErrorCode;
import org.openiam.connector.type.ResponseType;
import org.openiam.connector.type.StatusCodeType;
import org.openiam.connector.type.SuspendRequest;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.spml2.spi.common.SuspendCommand;
import org.openiam.spml2.spi.salesforce.dao.CallerDependentSalesForceDao;
import org.openiam.spml2.spi.salesforce.dao.SalesForceDao;
import org.openiam.spml2.spi.salesforce.exception.SalesForceDataIntegrityException;
import org.openiam.connector.util.ResponseBuilder;

import com.sforce.ws.ConnectionException;

public class SalesForceSuspendCommand extends AbstractSalesforceCommand implements SuspendCommand {

	@Override
	public ResponseType suspend(SuspendRequest request) {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);
        
        final String principalName = request.getUserIdentity();
        final String targetID = request.getTargetID();
        
        final ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);
        if(managedSys == null) {
        	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, String.format("No Managed System with target id: %s", targetID));
            return response;
        }

        if (StringUtils.isBlank(managedSys.getResourceId())) {
        	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, "ResourceID is not defined in the ManagedSys Object");
            return response;
        }

        final Resource res = resourceDataService.getResource(managedSys.getResourceId());
        if(res == null) {
        	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, "No resource for managed resource found");
            return response;
        }
        
        try {
        	final SalesForceDao dao = new CallerDependentSalesForceDao(managedSys.getUserId(), managedSys.getDecryptPassword(), managedSys.getConnectionString(), null);
        	dao.deleteByUserName(principalName);
		} catch (ConnectionException e) {
			log.error("Connection Exception", e);
			ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.AUTHENTICATION_FAILED, e.getMessage());
		} catch(SalesForceDataIntegrityException e) {
			log.error("SalesForce Exception", e);
			ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_ATTRIBUTE, e.getMessage());
		} catch(Throwable e) {
			log.error("Unkonwn error", e);
			ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.OTHER_ERROR, e.getMessage());
		}
		return response;
	}

}
