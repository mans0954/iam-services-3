import org.openiam.idm.groovy.helper.ServiceHelper;

def orgService = ServiceHelper.orgService();
output = null
if (user.companyId) {
    def org = orgService.getOrganization(user.companyId, null)
	if (org) {
		output = org.organizationName
	}
}