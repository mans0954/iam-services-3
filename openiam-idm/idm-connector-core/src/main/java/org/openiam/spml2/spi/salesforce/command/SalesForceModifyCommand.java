package org.openiam.spml2.spi.salesforce.command;

import java.text.ParseException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.ModificationType;
import org.openiam.spml2.msg.ModifyRequestType;
import org.openiam.spml2.msg.ModifyResponseType;
import org.openiam.spml2.msg.StatusCodeType;
import org.openiam.spml2.spi.common.ModifyCommand;
import org.openiam.spml2.spi.salesforce.exception.SalesForceDataIntegrityException;
import org.openiam.spml2.spi.salesforce.exception.SalesForcePersistException;
import org.openiam.spml2.util.msg.ResponseBuilder;

import com.sforce.ws.ConnectionException;

public class SalesForceModifyCommand extends AbstractSalesForceInsertCommand implements ModifyCommand {

	@Override
	public ModifyResponseType modify(final ModifyRequestType reqType) {
		final ModifyResponseType response = new ModifyResponseType();
	    response.setStatus(StatusCodeType.SUCCESS);

        final String targetID = reqType.getPsoID().getTargetID();

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
        
        final String principalName = reqType.getPsoID().getID();
        
        try {
        	final List<ModificationType> modTypeList = reqType.getModification();
        	if(CollectionUtils.isNotEmpty(modTypeList)) {
        		for (final ModificationType mod : modTypeList) {
        			final List<ExtensibleObject> objectList = mod.getData().getAny();
        			insertOrUpdate(principalName, objectList, managedSys);
        		}
        	}

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

}
