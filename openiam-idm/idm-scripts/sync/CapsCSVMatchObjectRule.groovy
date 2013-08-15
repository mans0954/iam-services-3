import org.apache.commons.lang.StringUtils
import org.openiam.idm.searchbeans.UserSearchBean
import org.openiam.idm.srvc.synch.dto.Attribute
import org.openiam.idm.srvc.synch.dto.SynchConfig
import org.openiam.idm.srvc.synch.service.MatchObjectRule
import org.openiam.idm.srvc.user.domain.UserEntity
import org.openiam.idm.srvc.user.dto.User
import org.openiam.idm.srvc.user.service.UserDataService
import org.springframework.context.ApplicationContext

class CapsCSVMatchObjectRule implements MatchObjectRule {

    ApplicationContext context

    def matchAttrName = null
    def matchAttrValue = null

    public User lookup(SynchConfig config, Map<String, Attribute> rowAttr) throws IllegalArgumentException {

        def searchBean = new UserSearchBean()
        matchAttrName = config.matchFieldName
        matchAttrValue = StringUtils.isNotBlank(config.customMatchAttr) ? rowAttr.get(config.customMatchAttr)?.value : null

        if (StringUtils.isBlank(matchAttrName) || StringUtils.isBlank(matchAttrValue)) {
            throw new IllegalArgumentException("matchAttrName and matchAttrValue can not be blank");
        }

        if (matchAttrName.equalsIgnoreCase("EMPLOYEE_ID")) {

            def customEmployeeId = matchAttrValue as Integer
            searchBean.employeeId = customEmployeeId as String

            def userManager = context?.getBean("userManager") as UserDataService
            List<UserEntity> userList = userManager.findBeans(searchBean)

            if (userList) {
                println "User matched with existing user..."
                return new User(userList.get(0).userId)
            }

        } else {
            throw new IllegalArgumentException("matchAttrName 'EMPLOYEE_ID' is supported only")
        }

        return null
    }

    public String getMatchAttrName() {
        return matchAttrName
    }

    public void setMatchAttrName(String matchAttrName) {
        this.matchAttrName = matchAttrName
    }

    public String getMatchAttrValue() {
        return matchAttrValue
    }

    public void setMatchAttrValue(String matchAttrValue) {
        this.matchAttrValue = matchAttrValue
    }

    ApplicationContext getContext() {
        return context
    }

    void setContext(ApplicationContext context) {
        this.context = context
    }

    @Override
    public String toString() {
        return "CapsCSVMatchObjectRule {" +
                ", matchAttrName='" + matchAttrName + '\'' +
                ", matchAttrValue='" + matchAttrValue + '\'' +
                '}'
    }
}
