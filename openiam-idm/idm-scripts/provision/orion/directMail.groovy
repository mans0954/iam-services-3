
println(">>>>>> directMail.groovy called.")
output = null

def organizationId = user.affiliations?.iterator()?.next()?.id
def primaryLogin = lg.login

if (organizationId) {
    def organizationService = context.getBean("organizationService")
    def domainName = organizationService?.getOrganization(organizationId)?.domainName
    output = primaryLogin +"@" + domainName
}

