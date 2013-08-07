package org.openiam.spml2.spi.salesforce.command;

import java.text.ParseException;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.spi.common.AddCommand;
import org.openiam.connector.salesforce.exception.SalesForceDataIntegrityException;
import org.openiam.connector.salesforce.exception.SalesForcePersistException;
import org.openiam.connector.util.ResponseBuilder;

import com.sforce.ws.ConnectionException;

@Deprecated
public class SalesforceAddCommand extends AbstractSalesForceInsertCommand implements AddCommand {

	@Override
	public ObjectResponse add(CrudRequest reqType) {
        final ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        final String targetID = reqType.getTargetID();
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
        
        final String principalName = reqType.getObjectIdentity();
		
        try {
			
        	final ExtensibleObject user = reqType.getExtensibleObject();
        	insertOrUpdate(principalName, user, managedSys);
			//com.sforce.soap.partner.sobject.SObject
			//partnerConnection.create(sObjects);
        } catch(SalesForcePersistException e) {
        	log.error("Sales Force Persist Exception", e);
			ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.PERSIST_EXCEPTION, e.getMessage());
		} catch (ConnectionException e) {
			log.error("Connection Exception", e);
			ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.AUTHENTICATION_FAILED, e.getMessage());
		} catch(ParseException e) {
			log.error("Parse Exception", e);
			ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.PARSE_EXCEPTION, e.getMessage());
		} catch(SalesForceDataIntegrityException e) {
			log.error("SalesForce Exception", e);
			ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_ATTRIBUTE, e.getMessage());
		} catch(Throwable e) {
			log.error("Unkonwn error", e);
			ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.OTHER_ERROR, e.getMessage());
		}
		return response;
	}

	/*
    public static void main(String[] args) {
    	final String sql = "SELECT Id FROM Profile WHERE Name='Standard Platform User'";
    	final String pwd = "foobar";
    	final String uname = "lev.bornovalov@openiam.com";
    	
		try {
			final ConnectorConfig connectorConfig = new ConnectorConfig();
			connectorConfig.setUsername(uname);
			connectorConfig.setPassword(pwd);
			connectorConfig.setAuthEndpoint("https://login.salesforce.com/services/Soap/u/22.0");
			final PartnerConnection partnerConnection = new PartnerConnection(connectorConfig);
			final QueryResult results = partnerConnection.query(sql);
			for(final SObject obj : results.getRecords()) {
				System.out.println(obj);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
    }
    */
}
