import org.openiam.base.AttributeOperationEnum
import org.openiam.dozer.converter.RoleDozerConverter
import org.openiam.idm.srvc.continfo.dto.Address
import org.openiam.idm.srvc.continfo.dto.EmailAddress
import org.openiam.idm.srvc.org.dto.Organization
import org.openiam.idm.srvc.role.service.RoleDataService

import java.text.SimpleDateFormat

import org.openiam.idm.srvc.synch.dto.Attribute
import org.openiam.idm.srvc.synch.dto.LineObject
import org.openiam.idm.srvc.synch.service.AbstractTransformScript
import org.openiam.idm.srvc.synch.service.TransformScript
import org.openiam.idm.srvc.user.dto.UserAttribute
import org.openiam.provision.dto.ProvisionUser
import org.openiam.idm.srvc.user.dto.UserStatusEnum
import org.openiam.idm.srvc.continfo.dto.Phone

import java.text.ParseException

public class TransformCSVRecord extends AbstractTransformScript {

    static String DOMAIN = "USR_SEC_DOMAIN"

    @Override
    void init() {}

    @Override
    public int execute(LineObject rowObj, ProvisionUser pUser) {

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

        pUser.employeeId = columnMap.get("EMPLOYEE_ID")?.value

        try {
            pUser.startDate = df.parse(columnMap.get("ORIG_HIRE_DT")?.value)
        } catch (ParseException parseException) {
            println(" Failed to parse hire date: " + parseException.getMessage())
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
        phone.countryCd = ''
        phone.areaCd = ''
        phone.phoneNbr = columnMap.get("EMPLOYEE_PHONE_NUMBER")?.value
        phone.metadataTypeId = "DESK_PHONE"
        addPhone(pUser, phone)

        // Processing role
        addRole(pUser, "End User")

    }

    def addEmailAddress(ProvisionUser pUser, EmailAddress emailAddress) {
        emailAddress.operation = AttributeOperationEnum.ADD
        if (!isNewUser) {
            for (EmailAddress e : pUser.emailAddresses) {
                if (e.metadataTypeId.equalsIgnoreCase(emailAddress.metadataTypeId)) {
                    e.updateEmailAddress(emailAddress)
                    e.operation = AttributeOperationEnum.REPLACE
                    return
                }
            }
        }
        pUser.emailAddresses.add(emailAddress)
    }

    def addOrganization(ProvisionUser pUser, Organization organization) {
        organization.operation = AttributeOperationEnum.ADD
        if (!isNewUser) {
            for (Organization e : pUser.affiliations) {
                if (e.organizationName == organization.organizationName &&
                        e.organizationTypeId == organization.organizationTypeId) {
                    return //exists, skip it
                }
            }
        }
        pUser.affiliations.add(organization)
    }

    def addRole(ProvisionUser pUser, String roleName) {
        def foundRole = pUser.roles.find { r-> r.roleName == roleName }
        if (!foundRole) {
            def roleDataService = context?.getBean("roleDataService") as RoleDataService
            def roleDozerConverter = context?.getBean("roleDozerConverter") as RoleDozerConverter
            def role = roleDozerConverter?.convertToDTO(roleDataService?.getRoleByName(roleName, null), false)
            if (role) {
                role.operation = AttributeOperationEnum.ADD
                pUser.roles.add(role)
            }
        }
    }

    def addAddress(ProvisionUser pUser, Address address) {
        address.operation = AttributeOperationEnum.ADD
        if (!isNewUser) {
            for (Address e : pUser.addresses) {
                if (e.metadataTypeId.equalsIgnoreCase(address.metadataTypeId)) {
                    e.updateAddress(address)
                    e.operation = AttributeOperationEnum.REPLACE
                    return
                }
            }
        }
        pUser.addresses.add(address)
    }

    def addPhone(ProvisionUser pUser, Phone phone) {
        phone.operation = AttributeOperationEnum.ADD
        if (!isNewUser) {
            for (Phone e : pUser.phones) {
                if (e.metadataTypeId.equalsIgnoreCase(phone.metadataTypeId)) {
                    e.updatePhone(phone)
                    e.operation = AttributeOperationEnum.REPLACE
                    return
                }
            }
        }
        pUser.phones.add(phone)
    }

    def addAttribute(ProvisionUser pUser, Attribute attr) {
        if (attr?.name) {
            def userAttr = new UserAttribute(attr.name, attr.value)
            userAttr.operation = AttributeOperationEnum.ADD
            if (!isNewUser) {
                for (String name : pUser.userAttributes.keySet()) {
                    if (name.equalsIgnoreCase(attr.name)) {
                        pUser.userAttributes.remove(name)
                        userAttr.operation = AttributeOperationEnum.REPLACE
                        break
                    }
                }
            }
            pUser.userAttributes.put(attr.name, userAttr)
        }
    }

}
