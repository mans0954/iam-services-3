import org.openiam.base.AttributeOperationEnum
import org.openiam.base.BaseAttribute
import org.openiam.base.BaseAttributeContainer
import org.openiam.idm.srvc.auth.login.LoginDataService

output = null

def loginManager = context.getBean("loginManager") as LoginDataService
def attributeContainer = new BaseAttributeContainer()
def oldSupervisorSet = (binding.hasVariable("userBeforeModify")) ? userBeforeModify.superiors : [] as Set
def supervisorSet = user.superiors as Set

supervisorSet?.each { s->
    if (!(s in oldSupervisorSet)) {
        s.operation = AttributeOperationEnum.ADD
    }
    def identity = loginManager.getByUserIdManagedSys(s.getUserId(), managedSysId)
    if (identity) {
        println("Adding supervisor id " + s.getUserId() + " --> " + identity.login)
        def id = matchParam.keyField + '=' + identity.login + ',' + matchParam.baseDn
        attributeContainer.attributeList.add(new BaseAttribute(id, id, s.operation))
    } else {
        println("Adding supervisor id " + s.getUserId() + " failed, no identity found for supervisor user")
    }
}
oldSupervisorSet?.each { s->
    if (!(s in supervisorSet)) {
        s.operation = AttributeOperationEnum.DELETE
        def identity = loginManager.getByUserIdManagedSys(s.getUserId(), managedSysId)
        if (identity) {
            println("Deleting supervisor id " + s.getUserId() + " --> " + identity.login)
            def id = matchParam.keyField + '=' + identity.login + ',' + matchParam.baseDn
            attributeContainer.attributeList.add(new BaseAttribute(id, id, s.operation))
        } else {
            println("Deleting supervisor id " + s.getUserId() + " failed, no identity found for supervisor user")
        }
    }
}
if (attributeContainer.attributeList) {
    output = attributeContainer
}
