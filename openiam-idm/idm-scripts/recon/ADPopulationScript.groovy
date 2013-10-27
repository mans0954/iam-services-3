import org.openiam.provision.dto.ProvisionUser
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleId;
import org.openiam.idm.srvc.user.dto.UserStatusEnum
import org.openiam.idm.srvc.synch.dto.Attribute
import org.openiam.idm.srvc.user.dto.UserAttribute
import org.openiam.idm.srvc.auth.dto.Login;

public class ADPopulationScript implements org.openiam.idm.srvc.recon.service.PopulationScript {
    public int execute(Map<String, String> line, ProvisionUser pUser){
        int retval = 1;
        for(String key: line.keySet()) {
            switch(key) {
                case "sAMAccountName":
                    addAttribute(pUser, "sAMAccountName", line.get("sAMAccountName"));
                    break
                case "cn":
                		//ignore for now
                    /*String[] parts = line.get("cn").split(" ")
                    if(parts.length == 2){
                        if(pUser.firstName != parts[0]){
                            pUser.firstName = parts[0]
                            retval = 0
                        }
                        if(pUser.lastName != parts[1]){
                            pUser.lastName = parts[1]
                            retval = 0
                        }
                    }*/
                    addAttribute(pUser, "cn", line.get("cn"));
                    break
                case "mail":
                    if(pUser.email != line.get("mail")){
                        pUser.email = line.get("mail")
                        retval = 0
                    }
                    addAttribute(pUser, "mail", line.get("mail"));
                    break
                case "o":
                    // fixed value for this LDAP managed sys
                    break
                case "ou":
                    // fixed value for this LDAP managed sys
                    addAttribute(pUser, "ou", line.get("ou"));
                    break
                case "employeeID":
                    if(pUser.employeeId != line.get("employeeID")){
                        pUser.employeeId = line.get("employeeID")
                        retval = 0
                    }
                    addAttribute(pUser, "employeeID", line.get("employeeID"));
                    break
                case "employeeType":
                    if(pUser.employeeType != line.get("employeeType")){
                        pUser.employeeType = line.get("employeeType")
                        retval = 0
                    }
                    addAttribute(pUser, "employeeType", line.get("employeeType"));
                    break
                case "postalCode":
                    if(pUser.postalCd != line.get("postalCode")){
                        pUser.postalCd = line.get("postalCode")
                        retval = 0
                    }
                    addAttribute(pUser, "postalCode", line.get("postalCode"));
                    break
                case "sn":
                    if(pUser.lastName != line.get("sn")){
                        pUser.lastName = line.get("sn")
                        retval = 0
                    }
                    addAttribute(pUser, "sn", line.get("sn"));
                    break
                case "st":
                    if(pUser.state != line.get("st")){
                        pUser.state = line.get("st")
                        retval = 0
                    }
                    addAttribute(pUser, "st", line.get("st"));
                    break
                case "userPassword":
                    // not supported yet
                    break
                case "streetAddress":
                    String[] parts = line.get("streetAddress").split(" ")
                    if(parts.length == 3){
                        if(pUser.bldgNum != parts[0]){
                            pUser.bldgNum = parts[0]
                            retval = 0
                        }
                        if(pUser.streetDirection != parts[1]){
                            pUser.streetDirection = parts[1]
                            retval = 0
                        }
                        if(pUser.address1 != parts[1]){
                            pUser.address1 = parts[1]
                            retval = 0
                        }
                    }
                    addAttribute(pUser, "streetAddress", line.get("streetAddress"));
                    break
                case "displayName":
                    String[] parts = line.get("displayName").split(",")
                    if(parts.length == 2){
                        if(pUser.firstName != parts[1]){
                            pUser.firstName = parts[1]
                            retval = 0
                        }
                        if(pUser.lastName != parts[0]){
                            pUser.lastName = parts[0]
                            retval = 0
                        }
                    }
                    addAttribute(pUser, "displayName", line.get("displayName"));
                    break
 
                case "title":
                    if(pUser.title != line.get("title")){
                        pUser.title = line.get("title")
                        retval = 0
                    }
                    addAttribute(pUser, "title", line.get("title"));
                    break
                case "givenName":
                    if(pUser.firstName != line.get("givenName")){
                        pUser.firstName = line.get("givenName")
                        retval = 0
                    }
                    addAttribute(pUser, "givenName", line.get("givenName"));
                    break
                case "userPrincipalName":
                    addAttribute(pUser, "userPrincipalName", line.get("userPrincipalName"));
                    break
            }
        }
        List<Role> roleList = pUser.getMemberOfRoles() != null ? pUser.getMemberOfRoles() : new LinkedList<Role>();
        boolean isEndUserRoleFound = false;
        RoleId endUserRoleId = new RoleId("USR_SEC_DOMAIN", "END_USER");

        for(Role role : roleList) {
            if(role.getId().equals(endUserRoleId)) {
                isEndUserRoleFound = true;
                break;
            }
        }
        if(!isEndUserRoleFound) {
            Role r = new Role();
            r.setId(endUserRoleId);
            roleList.add(r);
            pUser.setMemberOfRoles(roleList);
        }
        //set status to active: IMPORTANT!!!!
        pUser.setStatus(UserStatusEnum.PENDING_INITIAL_LOGIN);
        return retval;
    }

    private void addAttribute(ProvisionUser user, String attributeName, String attributeValue) {
       UserAttribute userAttr = new UserAttribute(attributeName, attributeValue);
       user.getUserAttributes().put(attributeName, userAttr);
    }
}