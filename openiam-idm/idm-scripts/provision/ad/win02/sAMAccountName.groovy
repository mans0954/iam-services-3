import org.openiam.idm.srvc.user.dto.UserAttribute

//if("EXIST".equals(targetSystemIdentityStatus)) {
//    return;
//}
UserAttribute attr = user.getUserAttributes().get("sAMAccountName");
if (attr != null && attr.value != null ) {
    output =  attr.value;
}else {
    output=user.employeeId;
}