
println(">>>>>> directMail.groovy called.")
output = null

def it = user.affiliations?.iterator()
def organizationId = it?.hasNext()? it.next()?.id : null
def primaryLogin = lg.login

if (organizationId) {
    def organizationService = context.getBean("organizationService")
    def domainName = organizationService?.getOrganization(organizationId)?.domainName
    output = primaryLogin +"@" + domainName
}

