import org.openiam.base.AttributeOperationEnum
import org.openiam.idm.srvc.audit.service.AuditLogService
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto
import org.openiam.idm.srvc.mngsys.service.ManagedSystemServiceImpl
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService
import org.openiam.idm.srvc.res.dto.Resource
import org.openiam.idm.srvc.res.service.ResourceDataService
import org.openiam.idm.srvc.role.service.RoleDataService
import org.openiam.idm.srvc.role.ws.RoleDataWebService
import org.openiam.provision.dto.ProvisionUser
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;

public class LDAPPopulationScript extends org.openiam.idm.srvc.recon.service.AbstractPopulationScript {
    public int execute(Map<String, String> line, ProvisionUser pUser){
        int retval = 1;
        for(String key: line.keySet()) {
            switch(key) {
                case "uid":
                    //ignore for now - if this is changed no match can be established
                    break
                case "cn":
                    String[] parts = line.get("cn").split(" ")
                    if(parts.length == 2){
                        if(pUser.firstName != parts[0]){
                            pUser.firstName = parts[0]
                            retval = 0
                        }
                        if(pUser.lastName != parts[1]){
                            pUser.lastName = parts[1]
                            retval = 0
                        }
                    }
                    break
                case "mail":
                    if(pUser.email != line.get("mail")){
                        pUser.setEmail(line.get("mail"))
                        retval = 0
                    }
                    break
                case "o":
                    // fixed value for this LDAP managed sys
                    break
                case "ou":
                    // fixed value for this LDAP managed sys
                    break
                case "postalCode":
                    if(pUser.postalCd != line.get("postalCode")){
                        pUser.postalCd = line.get("postalCode")
                        retval = 0
                    }
                    break
                case "sn":
                    if(pUser.lastName != line.get("sn")){
                        pUser.setLastName(line.get("sn"))
                        retval = 0
                    }
                    break
                case "st":
                    if(pUser.state != line.get("st")){
                        pUser.state = line.get("st")
                        retval = 0
                    }
                    break
                case "street":
                    String[] parts = line.get("street").split(" ")
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
                    break
                case "userPassword":
                    // not supported yet
                    break
                case "postalAddress":
                    String[] parts = line.get("postalAddress").split(" ")
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
                    break
                case "displayName":
                    String[] parts = line.get("displayName").split(",")
                    if(parts.length == 2){
                        if(pUser.firstName != parts[1]){
                            pUser.setFirstName(parts[1])
                            retval = 0
                        }
                        if(pUser.lastName != parts[0]){
                            pUser.setLastName(parts[0])
                            retval = 0
                        }
                    }
                    break
                case "employeeType":
                    if(pUser.employeeType != line.get("employeeType")){
                        pUser.setEmployeeType("employeeType")
                        retval = 0
                    }
                    break
                case "objectclass":
                    // fixed in this ldap managed sys
                    break
                case "title":
                    if(pUser.title != line.get("title")){
                        pUser.setTitle(line.get("title"))
                        retval = 0
                    }
                    break
                case "givenName":
                    if(pUser.firstName != line.get("givenName")){
                        pUser.setFirstName(line.get("givenName"))
                        retval = 0
                    }
                    break
            }
        }
        /* Set<Role> roleList = pUser.getRoles();
         RoleDataWebService dataService = context.getBean("roleWS");
         Role endUserRole = dataService.getRole("1","3000");
         if (!roleList.contains(endUserRole)) {
             endUserRole.setOperation(AttributeOperationEnum.ADD);
             pUser.getRoles().add(endUserRole);
         }*/
        ManagedSystemWebService systemWebService = context.getBean("managedSysService");
        ResourceDataService  resourceDataService = context.getBean("resourceDataService");
        ManagedSysDto currentManagedSys = systemWebService.getManagedSys(pUser.getSrcSystemId());
        Resource currentResource = resourceDataService.getResource(currentManagedSys.getResourceId());
        currentResource.setOperation(AttributeOperationEnum.ADD);
        pUser.getResources().add(currentResource);
        //set status to active: IMPORTANT!!!!
        pUser.setStatus(UserStatusEnum.PENDING_INITIAL_LOGIN);
        return retval;
    }
}