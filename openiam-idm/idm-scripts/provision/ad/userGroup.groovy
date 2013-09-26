import org.openiam.base.BaseAttribute
import org.openiam.base.BaseAttributeContainer
import org.openiam.idm.srvc.grp.dto.Group
import org.openiam.idm.srvc.role.dto.Role


def groupBaseDN = ",OU=idm-test,DC=ad,DC=openiamdemo,DC=info"

def groupSet = user.groups as Set

//user.roles?.each { Role r-> groupSet.addAll(r.groups) } //TODO: uncomment this if groups from roles needed

def attributeContainer = new BaseAttributeContainer()
output = null
groupSet?.each { Group g->
    println("Adding group id  " + g.grpId + " --> " + (g.grpName + groupBaseDN))
    def qualifiedGroupName = "cn=" + g.grpName + groupBaseDN
    attributeContainer.attributeList.add(new BaseAttribute(qualifiedGroupName, qualifiedGroupName, g.operation))
}
if (attributeContainer.attributeList) {
    output = attributeContainer
}