import org.openiam.base.AttributeOperationEnum
import org.openiam.base.BaseAttribute
import org.openiam.base.BaseAttributeContainer
import org.openiam.dozer.converter.GroupDozerConverter
import org.openiam.idm.srvc.grp.dto.Group
import org.openiam.idm.srvc.role.dto.Role
import org.openiam.idm.srvc.role.service.RoleDataService


def groupBaseDN = ",OU=idm-test,DC=ad,DC=openiamdemo,DC=info"

def oldGroupSet = userBeforeModify.groups as Set
def groupSet = user.groups as Set

oldGroupSet.addAll(getGroupsFromRoles(userBeforeModify.roles))
groupSet.addAll(getGroupsFromRoles(user.roles))

def attributeContainer = new BaseAttributeContainer()
output = null
groupSet?.each { Group g->
    if (!(g in oldGroupSet)) {
        g.operation = AttributeOperationEnum.ADD
    }
    println("Adding group id  " + g.grpId + " --> " + (g.grpName + groupBaseDN))
    def qualifiedGroupName = "cn=" + g.grpName + groupBaseDN
    attributeContainer.attributeList.add(new BaseAttribute(qualifiedGroupName, qualifiedGroupName, g.operation))
}
if (attributeContainer.attributeList) {
    output = attributeContainer
}

Set<Group> getGroupsFromRoles(Set<Role> roles) {
    def roleDataService = context?.getBean("roleDataService") as RoleDataService
    def groupDozerConverter = context?.getBean("groupDozerConverter") as GroupDozerConverter
    def groups = [] as Set
    roles.each {Role r->
        groups.addAll(groupDozerConverter.convertToDTOSet(roleDataService.getRole(r.roleId).groups, false))
    }
    return groups
}