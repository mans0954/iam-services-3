package org.openiam.idm.srvc.synch.srcadapter;

import java.util.List;
import java.util.Map;

import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.synch.dto.Attribute;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.service.MatchObjectRule;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("defaultMatchRule")
public class DefaultMatchObjectRule implements MatchObjectRule {

    @Autowired
	private UserMgr userManager;

    @Autowired
    private UserDozerConverter userDozerConverter;
	
	private String matchAttrName = null;
	private String matchAttrValue = null;

	public User lookup(SynchConfig config, Map<String, Attribute> rowAttr) {
		final UserSearchBean searchBean = new UserSearchBean();
		//UserSearch search = new UserSearch();
		//Map<String, UserAttribute> atMap = user.getUserAttributes();
		String srcFieldValue = rowAttr.get(config.getCustomMatchAttr()).getValue();
		matchAttrValue = srcFieldValue;
		matchAttrName = config.getMatchFieldName();
	
		
		if (config.getMatchFieldName().equalsIgnoreCase("USERID")) {
			searchBean.setUserId(srcFieldValue);
			//search.setUserId(srcFieldValue);
		}
		if (config.getMatchFieldName().equalsIgnoreCase("PRINCIPAL")) {
			searchBean.setPrincipal(srcFieldValue);
			//search.setPrincipal(srcFieldValue);
		}
		if (config.getMatchFieldName().equalsIgnoreCase("EMAIL")) {
			searchBean.setEmailAddress(srcFieldValue);
			//search.setEmailAddress(srcFieldValue);
		}	
		if (config.getMatchFieldName().equalsIgnoreCase("EMPLOYEE_ID")) {
			searchBean.setEmployeeId(srcFieldValue);
			//search.setEmployeeId(srcFieldValue);
		}		
		if (config.getMatchFieldName().equalsIgnoreCase("ATTRIBUTE")) {
			System.out.println("- cofiguring search by attribute..");
			System.out.println("- match attr=.." + config.getCustomMatchAttr());
		
				
			// get the attribute value from the data_set
			String valueToMatch = rowAttr.get(config.getCustomMatchAttr()).getValue();
			System.out.println("- src field value=.." + valueToMatch);
			matchAttrName = config.getCustomMatchAttr();
			matchAttrValue = valueToMatch;
			
			searchBean.setAttributeName(matchAttrName);
			searchBean.setAttributeValue(matchAttrValue);
			//search.setAttributeName(config.getCustomMatchAttr());
			//search.setAttributeValue(valueToMatch);
		}

		List<UserEntity> userList = userManager.findBeans(searchBean);

		if (userList != null && !userList.isEmpty()) {
			System.out.println("User matched with existing user...");
			User u = userDozerConverter.convertToDTO(userList.get(0), true);
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
