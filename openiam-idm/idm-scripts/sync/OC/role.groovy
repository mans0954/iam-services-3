import org.openiam.dozer.converter.RoleDozerConverter
import org.openiam.idm.srvc.role.service.RoleDataService

def roleName = attributeName
def foundRole = userRoleList.find { r-> r.roleName == roleName }
if (!foundRole) {
    def roleDataService = context?.getBean("roleDataService") as RoleDataService
    def roleDozerConverter = context?.getBean("roleDozerConverter") as RoleDozerConverter
    def role = roleDozerConverter?.convertToDTO(roleDataService?.getRoleByName(roleName, null), false)
    pUser.addMemberRole(role)
}
output = ""