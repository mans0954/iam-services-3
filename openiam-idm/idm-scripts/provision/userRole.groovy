import java.util.ArrayList;
import java.util.List;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.User;

import org.openiam.base.BaseAttribute;
import org.openiam.base.BaseAttributeContainer;


BaseAttributeContainer attributeContainer = new BaseAttributeContainer();

String roleBaseDN = "ou=roles,dc=openiam,dc=org";

List<String> roleStrList = new ArrayList<String>();
def List<Role> roleList = user.getMemberOfRoles();


if (roleList != null) {
	if (roleList.size() > 0)  {
		for (Role r : roleList) {
			
			String qualifiedRoleName = "cn=" + r.id.roleId + "," + roleBaseDN;
			attributeContainer.getAttributeList().add(new BaseAttribute(qualifiedRoleName, qualifiedRoleName, r.operation));
			
		
		}
		output = attributeContainer;
	}else {
		output = null;
	}
}else {
	output = null;
}

