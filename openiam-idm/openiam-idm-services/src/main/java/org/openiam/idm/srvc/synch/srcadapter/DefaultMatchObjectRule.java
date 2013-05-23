package org.openiam.idm.srvc.synch.srcadapter;

import java.util.List;
import java.util.Map;

import org.openiam.idm.srvc.synch.dto.Attribute;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.service.MatchObjectRule;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserSearch;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("defaultMatchRule")
public class DefaultMatchObjectRule implements MatchObjectRule {

    @Autowired
    @Qualifier("userWS")
	private UserDataWebService userManager;
	
	private String matchAttrName = null;
	private String matchAttrValue = null;

	public User lookup(SynchConfig config, Map<String, Attribute> rowAttr) {
		UserSearch search = new UserSearch();
		//Map<String, UserAttribute> atMap = user.getUserAttributes();
		String srcFieldValue = rowAttr.get(config.getCustomMatchAttr()).getValue();
		matchAttrValue = srcFieldValue;
		matchAttrName = config.getMatchFieldName();
	
		
		if (config.getMatchFieldName().equalsIgnoreCase("USERID")) {
			search.setUserId(srcFieldValue);
		}
		if (config.getMatchFieldName().equalsIgnoreCase("PRINCIPAL")) {
			search.setPrincipal(srcFieldValue);
		}
		if (config.getMatchFieldName().equalsIgnoreCase("EMAIL")) {
			search.setEmailAddress(srcFieldValue);
		}	
		if (config.getMatchFieldName().equalsIgnoreCase("EMPLOYEE_ID")) {
			search.setEmployeeId(srcFieldValue);
		}		
		if (config.getMatchFieldName().equalsIgnoreCase("ATTRIBUTE")) {
			System.out.println("- cofiguring search by attribute..");
			System.out.println("- match attr=.." + config.getCustomMatchAttr());
		
				
			// get the attribute value from the data_set
			String valueToMatch = rowAttr.get(config.getCustomMatchAttr()).getValue();
			System.out.println("- src field value=.." + valueToMatch);
			
			search.setAttributeName(config.getCustomMatchAttr());
			search.setAttributeValue(valueToMatch);
			
			//
			matchAttrName = search.getAttributeName();
			matchAttrValue = search.getAttributeValue();
			
		}

		List<User> userList = userManager.search(search);
		
		if (userList != null && !userList.isEmpty()) {
			System.out.println("User matched with existing user...");
			User u = userList.get(0);
			return u;
		}		
		return null;
	}

	public String getMatchAttrName() {
		return matchAttrName;
	}

	public void setMatchAttrName(String matchAttrName) {
		this.matchAttrName = matchAttrName;
	}

	public String getMatchAttrValue() {
		return matchAttrValue;
	}

	public void setMatchAttrValue(String matchAttrValue) {
		this.matchAttrValue = matchAttrValue;
	}

    @Override
    public String toString() {
        return "DefaultMatchObjectRule{" +
                "userManager=" + userManager +
                ", matchAttrName='" + matchAttrName + '\'' +
                ", matchAttrValue='" + matchAttrValue + '\'' +
                '}';
    }
}
