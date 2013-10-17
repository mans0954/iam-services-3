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

output = null
def primaryLogin = lg.login

def ldapDN
def organizationId = user.affiliations?.iterator()?.next()?.id
if (organizationId) {
    def organizationService = context.getBean("organizationService")
    ldapDN = organizationService?.getOrganization(organizationId)?.attributes?.find { it.name == 'LDAP_DN' }?.value
}

if (ldapDN) {
    output = matchParam.keyField + "=" + primaryLogin + "," + ldapDN
    println("OU found: dn= " + output)
} else {
    output = matchParam.keyField + "=" + primaryLogin + "," + matchParam.baseDn
    println("No OU found: dn= " + output)
}
	
