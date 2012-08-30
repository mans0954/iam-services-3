package org.openiam.spml2.spi.salesforce;

import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.mngsys.dto.ManagedSys;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.AddRequestType;
import org.openiam.spml2.msg.AddResponseType;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.StatusCodeType;
import org.openiam.spml2.spi.common.AddCommand;
import org.openiam.spml2.spi.salesforce.dao.CallerDependentSalesForceDao;
import org.openiam.spml2.spi.salesforce.dao.SalesForceDao;
import org.openiam.spml2.spi.salesforce.exception.SalesForceDataIntegrityException;
import org.openiam.spml2.spi.salesforce.exception.SalesForcePersistException;
import org.openiam.spml2.spi.salesforce.model.User;
import org.openiam.spml2.util.msg.ResponseBuilder;

import com.sforce.soap.partner.DeleteResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.UndeleteResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class SalesforceAddCommand extends AbstractSalesForceInsertCommand implements AddCommand {

	@Override
	public AddResponseType add(AddRequestType reqType) {
        final AddResponseType response = new AddResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String targetID = reqType.getTargetID();
        final ManagedSys managedSys = managedSysService.getManagedSys(targetID);
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
			
        	final List<ExtensibleObject> objectList = reqType.getData().getAny();
        	insertOrUpdate(principalName, objectList, managedSys);
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

	
    public static void main(String[] args) {
    	final String sql = "SELECT id, EmailEncodingKey, Alias, Email, TimeZoneSidKey, DefaultGroupNotificationFrequency, Username, LanguageLocaleKey, ProfileId, LocaleSidKey, DigestFrequency, LastName FROM User";
    	final String pwd = "foobar";
    	final String uname = "lev.bornovalov@openiam.com";
    	
		try {
			final SalesForceDao dao = new CallerDependentSalesForceDao(uname, pwd, "https://login.salesforce.com/services/Soap/u/22.0", new HashSet<String>());
			
			final String random = "test_" + RandomStringUtils.randomAlphanumeric(2);
			final String userName = random + "@email.com";
			
			final User user = new User(userName);
			user.setAlias("foobar");
			user.setProfileId("00ed0000000xyLc");
			user.setLastName(random);
			
			System.out.println("Saving...");
			dao.saveOrUpdate(user);
			
			System.out.println("Querying...");
			System.out.println(dao.findByUserName(userName));
			
			System.out.println("Updating...");
			dao.saveOrUpdate(user);
			
			System.out.println("Querying...");
			System.out.println(dao.findByUserName(userName));
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
    }
}
