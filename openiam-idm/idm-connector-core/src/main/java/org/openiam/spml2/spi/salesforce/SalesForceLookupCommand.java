package org.openiam.spml2.spi.salesforce;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.connector.type.*;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.spml2.spi.common.LookupCommand;
import org.openiam.spml2.spi.salesforce.dao.CallerDependentSalesForceDao;
import org.openiam.spml2.spi.salesforce.dao.SalesForceDao;
import org.openiam.spml2.spi.salesforce.exception.SalesForceDataIntegrityException;
import org.openiam.spml2.spi.salesforce.exception.SalesForcePersistException;
import org.openiam.spml2.spi.salesforce.model.User;
import org.openiam.connector.util.ResponseBuilder;

import com.sforce.ws.ConnectionException;
import com.sforce.ws.bind.XmlObject;

public class SalesForceLookupCommand extends AbstractSalesforceCommand implements LookupCommand {

	@Override
	public SearchResponse lookup(LookupRequest reqType) {
		final SearchResponse response = new SearchResponse();
        response.setStatus(StatusCodeType.SUCCESS);
		
        final String targetID = reqType.getTargetID();
        final String principalName = reqType.getSearchValue();

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
			final List<ExtensibleAttribute> attrList = reqType.getRequestedAttributes();
			final Set<String> fieldNames = new HashSet<String>();
			
			/* only attempt to fetch if you can construct a Select query, since SalesForce does not allow 'SELECT *' queries */
            if (CollectionUtils.isNotEmpty(attrList)) {
                for (final ExtensibleAttribute att : attrList) {
                    fieldNames.add(att.getName());
                }
            }

            final SalesForceDao dao = new CallerDependentSalesForceDao(managedSys.getUserId(), managedSys.getDecryptPassword(),  managedSys.getConnectionString(), fieldNames);
			final User user = dao.findByUserName(principalName);
			if(user != null) {
				final UserValue userValue = new UserValue();
                userValue.setUserIdentity(principalName);
                userValue.getAttributeList().add(new ExtensibleAttribute("id", user.getId()));
				for(final Iterator<XmlObject> it = user.getChildren(); it.hasNext();) {
					final XmlObject node = it.next();
                    userValue.getAttributeList().add(new ExtensibleAttribute(node.getName().getLocalPart(), (node.getValue() != null) ? node.getValue().toString() : null));
				}
				response.getUserList().add(userValue);
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
