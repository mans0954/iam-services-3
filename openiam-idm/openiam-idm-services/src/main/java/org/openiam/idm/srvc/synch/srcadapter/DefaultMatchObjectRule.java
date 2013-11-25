package org.openiam.idm.srvc.synch.srcadapter;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.recon.dto.MatchConfig;
import org.openiam.idm.srvc.synch.dto.Attribute;
import org.openiam.idm.srvc.synch.service.MatchObjectRule;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("defaultMatchRule")
public class DefaultMatchObjectRule implements MatchObjectRule {
    private static final Log log = LogFactory.getLog(DefaultMatchObjectRule.class);
    @Autowired
	private UserDataService userManager;

    @Autowired
    private UserDozerConverter userDozerConverter;
	
	private String matchAttrName = null;
	private String matchAttrValue = null;

	public User lookup(MatchConfig matchConfig, Map<String, Attribute> rowAttr) throws IllegalArgumentException {
		final UserSearchBean searchBean = new UserSearchBean();
		//UserSearch search = new UserSearch();
		//Map<String, UserAttribute> atMap = user.getUserAttributes();
        matchAttrName = matchConfig.getMatchFieldName();
		matchAttrValue = (StringUtils.isNotBlank(matchConfig.getCustomMatchAttr())) ? rowAttr.get(matchConfig.getCustomMatchAttr()).getValue() : null;

        if (StringUtils.isBlank(matchAttrName) || StringUtils.isBlank(matchAttrValue)) {
            throw new IllegalArgumentException("matchAttrName and matchAttrValue can not be blank");
        }

		if (matchAttrName.equalsIgnoreCase("USERID")) {
			searchBean.setUserId(matchAttrValue);
			//search.setUserId(matchAttrValue);

		} else if (matchAttrName.equalsIgnoreCase("PRINCIPAL")) {
            LoginSearchBean lsb = new LoginSearchBean();
            lsb.setLogin(matchAttrValue);
            lsb.setManagedSysId(matchConfig.getManagedSysId());
			searchBean.setPrincipal(lsb);
			//search.setPrincipal(matchAttrValue);

		} else if (matchAttrName.equalsIgnoreCase("EMAIL")) {
			searchBean.setEmailAddress(matchAttrValue);
			//search.setEmailAddress(matchAttrValue);

		} else if (matchAttrName.equalsIgnoreCase("EMPLOYEE_ID")) {
			searchBean.setEmployeeId(matchAttrValue);
			//search.setEmployeeId(matchAttrValue);

		} else if (matchAttrName.equalsIgnoreCase("ATTRIBUTE")) {
			System.out.println("- cofiguring search by attribute..");
			System.out.println("- match attr=.." + matchConfig.getMatchSrcFieldName());
		
			// get the attribute value from the data_set
			System.out.println("- src field value=.." + matchAttrValue);
			matchAttrName = matchConfig.getMatchSrcFieldName();

			searchBean.setAttributeName(matchAttrName);
			searchBean.setAttributeValue(matchAttrValue);

		}

        List<UserEntity> userList = null;

        userList = userManager.getByExample(searchBean, 0, Integer.MAX_VALUE);


        if (userList != null && !userList.isEmpty()) {
			System.out.println("User matched with existing user...");
			return new User(userList.get(0).getUserId());
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
