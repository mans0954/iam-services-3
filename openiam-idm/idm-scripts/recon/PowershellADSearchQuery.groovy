//import org.openiam.idm.srvc.role.dto.RoleAttribute
output="Get-ADUser -Filter {Name -like '*'} -Properties *";
/*if(role != null) {
    Set<RoleAttribute> attributes =  role.getRoleAttributes();
    for(RoleAttribute attr : attributes) {
        if("AD_OU".equals(attr.getName())) {
            output="Get-ADUser -Filter {Name -like '*'} -SearchBase '"+attr.getValue()+"' -Properties * -searchscope 1";
            break;
        };
    }
} else if(group != null) {
    output="Get-ADGroupMember -Identity "+group.grpName;
}*/
