import org.openiam.dozer.converter.OrganizationDozerConverter
import org.openiam.dozer.converter.RoleDozerConverter
import org.openiam.idm.searchbeans.OrganizationSearchBean
import org.openiam.idm.srvc.continfo.dto.Address
import org.openiam.idm.srvc.continfo.dto.EmailAddress
import org.openiam.idm.srvc.org.service.OrganizationService
import org.openiam.idm.srvc.role.service.RoleDataService
import org.openiam.idm.srvc.user.service.UserDataService

import java.text.SimpleDateFormat

import org.openiam.idm.srvc.synch.dto.Attribute
import org.openiam.idm.srvc.synch.dto.LineObject
import org.openiam.idm.srvc.synch.service.AbstractTransformScript
import org.openiam.idm.srvc.synch.service.TransformScript
import org.openiam.idm.srvc.user.dto.UserAttribute
import org.openiam.provision.dto.ProvisionUser
import org.openiam.idm.srvc.user.dto.UserStatusEnum
import org.openiam.idm.srvc.continfo.dto.Phone
import org.openiam.idm.srvc.org.service.OrganizationDataService

import javax.xml.namespace.QName
import javax.xml.ws.Service
import javax.xml.ws.soap.SOAPBinding
import java.text.ParseException

public class TransformCapsCSVRecord extends AbstractTransformScript {

    static String BASE_URL = "http://localhost:9090/openiam-esb/idmsrvc"

    static String DOMAIN = "USR_SEC_DOMAIN"

    @Override
    void init() {}

    @Override
    public int execute(LineObject rowObj, ProvisionUser pUser) {

        /* constants - maps to a managed sys id*/
        def MANAGED_SYS_ID = "110"
        def defaultRole = "END_USER"

        populateObject(rowObj, pUser)

        pUser.status = UserStatusEnum.ACTIVE
        pUser.securityDomain = "0"

        return TransformScript.NO_DELETE
    }

    private void populateObject(LineObject rowObj, ProvisionUser pUser) {

        def attrVal
        def df =  new SimpleDateFormat("MM/dd/yy")
        def columnMap =  rowObj.columnMap

        if (isNewUser) {
            pUser.userId = null
        }

        attrVal = columnMap.get("FORMATTED_NM")
        if (attrVal) {
            def names = attrVal.value.split(', ')
            if (names.size() > 0) {
                pUser.lastName = names[0]
            }
            if (names.size() > 1) {
                def names2 = names[1].split(' ')
                if (names2.size() > 0) {
                    pUser.firstName = names2[0]
                }
                if (names2.size() > 1) {
                    pUser.middleInit = names2[1]
                }
            }
        }

        def customEmployeeId = columnMap.get("EMPLOYEE_ID")?.value as Integer
        pUser.employeeId = customEmployeeId as String

        try {
            pUser.startDate = df.parse(columnMap.get("ORIG_HIRE_DT")?.value)
        } catch (ParseException parseException) {
            println(" Failed to parse birth date: " + parseException.getMessage())
        }
        try {
            pUser.birthdate = df.parse(columnMap.get("BIRTH_DT")?.value)
        } catch (ParseException parseException) {
            println(" Failed to parse birth date: " + parseException.getMessage())
        }
        pUser.sex = columnMap.get("SEX_ID")?.value
        pUser.title = columnMap.get("TITL_LONG_DD")?.value

        // Processing address
        def address = new Address()
        address.name = "PRIMARY_LOCATION"
        address.address1 = columnMap.get("STR_1_NM")?.value
        address.address2 = columnMap.get("STR_2_NM")?.value
        address.city = columnMap.get("CITY_NM")?.value
        address.postalCd = columnMap.get("ZIP")?.value
        address.state = columnMap.get("ST_CD")?.value
        address.country = columnMap.get("CTRY_CD")?.value
        address.metadataTypeId = "PRIMARY_LOCATION"
        addAddress(pUser, address)

        // Processing phone
        def phone = new Phone()
        phone.name = "DESK PHONE"
        phone.phoneNbr = columnMap.get("EMPLOYEE_PHONE_NUMBER")?.value
        phone.metadataTypeId = "DESK_PHONE"
        addPhone(pUser, phone)

        // Processing organizations
        addOCOrganization(pUser, columnMap.get("HOME_DEPT_CD")?.value)

        // Processing role
        addRole(pUser, "END_USER")

    }

    def addEmailAddress(ProvisionUser pUser, EmailAddress emailAddress)     {
        def emailAddresses = []
        if (!isNewUser) {
            def userManager = context?.getBean("userManager") as UserDataService
            emailAddresses = userManager.getEmailAddressDtoList(user.userId, false)
        }
        if (emailAddresses) {
            pUser.emailAddresses = emailAddresses
        }
        for (EmailAddress e : pUser.emailAddresses) {
            if (emailAddress.metadataTypeId.equalsIgnoreCase(e.metadataTypeId)) {
                e.updateEmailAddress(emailAddress)
                return
            }
        }
        pUser.emailAddresses.add(emailAddress)
    }

    def addOCOrganization(ProvisionUser pUser, String homeDeptCd) {
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
            deptSearchBean.organizationTypeId = "DIVISION"
            def deptList = organizationService.findBeans(deptSearchBean, null, 0, 1)
            if (deptList) {
                def department = organizationDozerConverter?.convertToDTO(deptList.get(0), false)
                pUser.addUserAffiliation(department)
            }
        }
    }

    def addRole(ProvisionUser pUser, String roleName) {
        def foundRole = userRoleList.find { r-> r.roleName == roleName }
        if (!foundRole) {
            def roleDataService = context?.getBean("roleDataService") as RoleDataService
            def roleDozerConverter = context?.getBean("roleDozerConverter") as RoleDozerConverter
            def role = roleDozerConverter?.convertToDTO(roleDataService?.getRoleByName(roleName, null), false)
            pUser.addMemberRole(role)
        }
    }

    def addAddress(ProvisionUser pUser, Address address) {
        def addresses = []
        if (!isNewUser) {
            def userManager = context?.getBean("userManager") as UserDataService
            addresses = userManager.getAddressDtoList(user.userId, false)
        }
        if (addresses) {
            pUser.addresses = addresses
        }
        for (Address a : pUser.addresses) {
            if (address.metadataTypeId.equalsIgnoreCase(a.metadataTypeId)) {
                a.updateAddress(address)
                return
            }
        }
        pUser.addresses.add(address)
    }

    def addPhone(ProvisionUser pUser, Phone phone) {
        def phones = []
        if (!isNewUser) {
            def userManager = context?.getBean("userManager") as UserDataService
            phones = userManager.getPhoneDtoList(user.userId, false)
        }
        if (phones) {
            pUser.phones = phones
        }
        for (Phone p : pUser.phones) {
            if (phone.metadataTypeId.equalsIgnoreCase(p.metadataTypeId)) {
                p.updatePhone(phone)
                return
            }
        }
        pUser.phones.add(phone)
    }

    def addAttribute(ProvisionUser pUser, Attribute attr) {
        if (!attr?.name?.isEmpty()) {
            def userAttr = new UserAttribute(attr.name, attr.value)
            pUser.userAttributes.put(attr.name, userAttr)
        }
    }

    static OrganizationDataService orgService() {
        String serviceUrl = BASE_URL + "/OrganizationDataService"
        String port ="OrganizationDataWebServicePort"
        String nameSpace = "urn:idm.openiam.org/srvc/org/service"

        Service service = Service.create(QName.valueOf(serviceUrl))

        service.addPort(new QName(nameSpace,port),
                SOAPBinding.SOAP11HTTP_BINDING,	serviceUrl)

        return service.getPort(new QName(nameSpace,	port),
                OrganizationDataService.class)
    }

}
