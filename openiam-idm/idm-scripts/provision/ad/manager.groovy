
/**
 * Manager.groovy
 * Returns the name of a person immediate supervisor. Return the DN so that it works with AD
 */

def loginManager = context.getBean("loginManager")
def userMgr = context.getBean("userWS")

output = null;

// user is passed into the script as a bind variable in the service that calls this script

def supVisorList = userMgr.getSupervisors(user.userId)
if (supVisorList) {
    def supervisor = supVisorList.get(0);

    def l = loginManager.getByUserIdManagedSys(supervisor.supervisor.userId, managedSysId)

    // identity for the AD resource
    output = l.login;

}


