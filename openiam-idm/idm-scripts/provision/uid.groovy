import org.openiam.idm.srvc.auth.login.LoginDataService

/*
Objects that are passed to the script:
sysId - DefaultManagedSysId
user - New user object that has been submitted to the provisioning service
org - Organization object		
context - Spring application context. Allows you to look up any spring bean
targetSystemIdentityStatus
targetSystemIdentity
targetSystemAttributes = attributes at the target system
*/

println("uid.groovy called.")
println(">>>>> user " + user.toString())

def loginManager = context.getBean("loginManager") as LoginDataService

def primaryLogin = lg.login
def loginId = matchParam.keyField + "=" + primaryLogin + "," + matchParam.baseDn
if (binding.hasVariable("managedSysId")) {
    def ctr = 0
    while (loginManager.loginExists( "USR_SEC_DOMAIN", loginId, managedSysId)) {
        strCtrSize = ctr as String
        loginId = matchParam.keyField + "=" +  primaryLogin + ctr + "," + matchParam.baseDn
        ctr++
    }
}
output = loginId