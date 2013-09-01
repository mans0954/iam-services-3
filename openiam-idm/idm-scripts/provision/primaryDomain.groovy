import org.openiam.idm.srvc.role.dto.Role;

List<Role> userRoles = user.memberOfRoles;
if (userRoles == null || userRoles.isEmpty()) {
	output="USR_SEC_DOMAIN";
}else {
	Role rl = userRoles.get(0);
	System.out.println(rl);
    def attr = rl.serviceId;
    if (attr?.value) {
        output =  attr.value;
    } else {
        output = "USR_SEC_DOMAIN";
    }
}
