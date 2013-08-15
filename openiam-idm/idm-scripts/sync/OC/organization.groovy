import org.openiam.dozer.converter.OrganizationDozerConverter
import org.openiam.idm.searchbeans.OrganizationSearchBean
import org.openiam.idm.srvc.org.service.OrganizationService

def homeDeptCd = attribute.value

def organizationService = context?.getBean("organizationService") as OrganizationService
def organizationDozerConverter = context?.getBean("organizationDozerConverter") as OrganizationDozerConverter

def orgSearchBean = new OrganizationSearchBean()
orgSearchBean.organizationName = "County of Orange"
orgSearchBean.organizationTypeId = "ORGANIZATION"
def orgList = organizationService.findBeans(orgSearchBean, null, 0, 1)
if (orgList) {
    def organization = organizationDozerConverter?.convertToDTO(orgList.get(0), false)
    pUser.addUserAffiliation(organization)
}

if (homeDeptCd) {
    def deptSearchBean = new OrganizationSearchBean()
    deptSearchBean.internalOrgId = homeDeptCd.substring(1)
    deptSearchBean.organizationTypeId = "DEPARTMENT"
    def deptList = organizationService.findBeans(deptSearchBean, null, 0, 1)
    if (deptList) {
        def department = organizationDozerConverter?.convertToDTO(deptList.get(0), false)
        pUser.addUserAffiliation(department)
    }
}
output = ""