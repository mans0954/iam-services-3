package org.openiam.spml2.spi.salesforce.command;

import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.SearchResponse;

@Deprecated
public class SalesForceLookupCommand extends AbstractSalesforceCommand {

	public SearchResponse lookup(LookupRequest reqType) {
		final SearchResponse response = new SearchResponse();
        response.setStatus(StatusCodeType.SUCCESS);
		
//        final String targetID = reqType.getPsoID().getTargetID();
//        final String principalName = reqType.getPsoID().getID();
//
//        final ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);
//        if(managedSys == null) {
//        	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, String.format("No Managed System with target id: %s", targetID));
//            return response;
//        }
//
//        if (StringUtils.isBlank(managedSys.getResourceId())) {
//        	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, "ResourceID is not defined in the ManagedSys Object");
//            return response;
//        }
//
//        final Resource res = resourceDataService.getResource(managedSys.getResourceId());
//        if(res == null) {
//        	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, "No resource for managed resource found");
//            return response;
//        }
//
//		try {
//			final List<ExtensibleObject> objectList = reqType.getAny();
//			final Set<String> fieldNames = new HashSet<String>();
//
//			/* only attempt to fetch if you can construct a Select query, since SalesForce does not allow 'SELECT *' queries */
//			if(CollectionUtils.isNotEmpty(objectList)) {
//				for (final ExtensibleObject obj : objectList) {
//					final List<ExtensibleAttribute> attrList = obj.getAttributes();
//					if(CollectionUtils.isNotEmpty(attrList)) {
//						for (final ExtensibleAttribute att : attrList) {
//							fieldNames.add(att.getName());
//						}
//					}
//				}
//			}
//
//			final SalesForceDao dao = new CallerDependentSalesForceDao(managedSys.getUserId(), managedSys.getDecryptPassword(),  managedSys.getConnectionString(), fieldNames);
//			final User user = dao.findByUserName(principalName);
//			if(user != null) {
//				final ExtensibleObject resultObject = new ExtensibleObject();
//				resultObject.setObjectId(principalName);
//				resultObject.getAttributes().add(new ExtensibleAttribute("id", user.getId()));
//				for(final Iterator<XmlObject> it = user.getChildren(); it.hasNext();) {
//					final XmlObject node = it.next();
//					resultObject.getAttributes().add(new ExtensibleAttribute(node.getName().getLocalPart(), (node.getValue() != null) ? node.getValue().toString() : null));
//				}
//				response.getAny().add(resultObject);
//			}
//        } catch(SalesForcePersistException e) {
//        	log.error("Sales Force Persist Exception", e);
//			ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.PERSIST_EXCEPTION, e.getMessage());
//		} catch (ConnectionException e) {
//			log.error("Connection Exception", e);
//			ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.AUTHENTICATION_FAILED, e.getMessage());
//		} catch(SalesForceDataIntegrityException e) {
//			log.error("SalesForce Exception", e);
//			ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_ATTRIBUTE, e.getMessage());
//		} catch(Throwable e) {
//			log.error("Unkonwn error", e);
//			ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.OTHER_ERROR, e.getMessage());
//		}
		return response;
	}

}
