import org.openiam.dozer.converter.OrganizationDozerConverter
import org.openiam.idm.searchbeans.OrganizationSearchBean
import org.openiam.idm.srvc.org.service.OrganizationService

def homeDeptCd = attribute.value

def organizationService = context?.getBean("organizationService") as OrganizationService
def organizationDozerConverter = context?.getBean("organizationDozerConverter") as OrganizationDozerConverter
def orgEntity = organizationService?.getOrganizationByName("County of Orange", null)
def organization = organizationDozerConverter?.convertToDTO(orgEntity, false)
if (organization) {
    pUser.addUserAffiliation(organization)
}
if (homeDeptCd) {
    def searchBean = new OrganizationSearchBean()
    searchBean.internalOrgId = homeDeptCd
    def list = organizationService.findBeans(searchBean, null, 0, 1)
    if (list) {
        def department = organizationDozerConverter?.convertToDTO(list.get(0), false)
        pUser.addUserAffiliation(department)
    }
}
output = ""