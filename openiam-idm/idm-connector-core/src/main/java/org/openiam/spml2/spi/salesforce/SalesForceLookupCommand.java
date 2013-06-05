package org.openiam.spml2.spi.salesforce;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.LookupRequestType;
import org.openiam.spml2.msg.LookupResponseType;
import org.openiam.spml2.msg.StatusCodeType;
import org.openiam.spml2.spi.common.LookupCommand;
import org.openiam.spml2.spi.salesforce.dao.CallerDependentSalesForceDao;
import org.openiam.spml2.spi.salesforce.dao.SalesForceDao;
import org.openiam.spml2.spi.salesforce.exception.SalesForceDataIntegrityException;
import org.openiam.spml2.spi.salesforce.exception.SalesForcePersistException;
import org.openiam.spml2.spi.salesforce.model.User;
import org.openiam.spml2.util.msg.ResponseBuilder;

import com.sforce.ws.ConnectionException;
import com.sforce.ws.bind.XmlObject;

public class SalesForceLookupCommand extends AbstractSalesforceCommand implements LookupCommand {

	@Override
	public LookupResponseType lookup(LookupRequestType reqType) {
		final LookupResponseType response = new LookupResponseType();
        response.setStatus(StatusCodeType.SUCCESS);
		
        final String targetID = reqType.getPsoID().getTargetID();
        final String principalName = reqType.getPsoID().getID();

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
			final List<ExtensibleObject> objectList = reqType.getAny();
			final Set<String> fieldNames = new HashSet<String>();
			
			/* only attempt to fetch if you can construct a Select query, since SalesForce does not allow 'SELECT *' queries */
			if(CollectionUtils.isNotEmpty(objectList)) {
				for (final ExtensibleObject obj : objectList) {
					final List<ExtensibleAttribute> attrList = obj.getAttributes();
					if(CollectionUtils.isNotEmpty(attrList)) {
						for (final ExtensibleAttribute att : attrList) {
							fieldNames.add(att.getName());
						}
					}
				}
			}
				
			final SalesForceDao dao = new CallerDependentSalesForceDao(managedSys.getUserId(), managedSys.getDecryptPassword(),  managedSys.getConnectionString(), fieldNames);
			final User user = dao.findByUserName(principalName);
			if(user != null) {
				final ExtensibleObject resultObject = new ExtensibleObject();
				resultObject.setObjectId(principalName);
				resultObject.getAttributes().add(new ExtensibleAttribute("id", user.getId()));
				for(final Iterator<XmlObject> it = user.getChildren(); it.hasNext();) {
					final XmlObject node = it.next();
					resultObject.getAttributes().add(new ExtensibleAttribute(node.getName().getLocalPart(), (node.getValue() != null) ? node.getValue().toString() : null));
				}
				response.getAny().add(resultObject);
			}
        } catch(SalesForcePersistException e) {
        	log.error("Sales Force Persist Exception", e);
			ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.PERSIST_EXCEPTION, e.getMessage());
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
