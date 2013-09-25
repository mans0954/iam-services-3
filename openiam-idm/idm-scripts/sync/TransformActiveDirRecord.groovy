import org.openiam.base.AttributeOperationEnum
import org.openiam.dozer.converter.OrganizationDozerConverter
import org.openiam.dozer.converter.RoleDozerConverter
import org.openiam.idm.searchbeans.OrganizationSearchBean
import org.openiam.idm.srvc.continfo.dto.Address
import org.openiam.idm.srvc.org.service.OrganizationService
import org.openiam.idm.srvc.role.service.RoleDataService

import org.openiam.idm.srvc.synch.dto.Attribute
import org.openiam.idm.srvc.synch.dto.LineObject
import org.openiam.idm.srvc.synch.service.AbstractTransformScript
import org.openiam.idm.srvc.synch.service.TransformScript
import org.openiam.idm.srvc.user.dto.UserAttribute
import org.openiam.provision.dto.ProvisionUser
import org.openiam.idm.srvc.user.dto.UserStatusEnum
import org.openiam.idm.srvc.auth.dto.Login
import org.openiam.idm.srvc.continfo.dto.Phone
import org.openiam.idm.srvc.continfo.dto.EmailAddress


public class TransformActiveDirRecord extends AbstractTransformScript {

    @Override
    void init() {}

    /* constants - maps to a managed sys id*/
    String DOMAIN = "USR_SEC_DOMAIN"
    String AD_MANAGED_SYS_ID = "110"
    
    String defaultRole = "End User"
    boolean assignDefaultRole = false
    
    boolean KEEP_AD_ID = true
   
    String IDENTITY_ATTRIBUTE = "sAMAccountName"
   
   //String IDENTITY_ATTRIBUTE = "userPrincipalName"
   //String IDENTITY_ATTRIBUTE = "distinguishedName"

	public int execute(LineObject rowObj, ProvisionUser pUser) {

        println("Is New User: " + isNewUser)
        if (!isNewUser) {
            println("User Object:" + user)
            println("PrincipalList: " + principalList)
            println("User Roles:" + userRoleList)
        } else {
            pUser.userId = null
        }

		populateObject(rowObj, pUser)

		pUser.setStatus(UserStatusEnum.ACTIVE)
		pUser.securityDomain = "0"
		// Add default role
		if (assignDefaultRole) {
            addRole(pUser, defaultRole)
		}
		return TransformScript.NO_DELETE
	}

    private void populateObject(LineObject rowObj, ProvisionUser pUser) {

        def attrVal
        Map<String, Attribute> columnMap = rowObj.columnMap

        attrVal = columnMap.get(IDENTITY_ATTRIBUTE)
        if (attrVal) {
            addAttribute(pUser, attrVal)
        }

        attrVal = columnMap.get("company")
        if (attrVal) {
            String orgName = attrVal.value
            if (orgName) {
                addOrganization(pUser, orgName, "ORGANIZATION")
            }
        }

        attrVal = columnMap.get("department")
        if (attrVal) {
            String orgName = attrVal.value
            if (orgName) {
                addOrganization(pUser, orgName, "DEPARTMENT")
            }
        }

		attrVal = columnMap.get("givenName")
		if (attrVal) {
			pUser.firstName = attrVal.value
		}

		attrVal = columnMap.get("sn")
        if (attrVal) {
            pUser.lastName = attrVal.value
        }

        attrVal = columnMap.get("title")
        if (attrVal) {
            pUser.title = attrVal.value
        }

        attrVal = columnMap.get("employeeID")
        if (attrVal) {
            pUser.employeeId = attrVal.value
        }

        attrVal = columnMap.get("ou")
        if (attrVal != null) {
            addAttribute(pUser, attrVal)
        }

        attrVal = columnMap.get("mail")
        if (attrVal) {
            // Processing email address
            def emailAddress = new EmailAddress()
            emailAddress.name = "PRIMARY_EMAIL"
            emailAddress.isDefault = true
            emailAddress.isActive = true
            emailAddress.emailAddress = attrVal.value
            emailAddress.metadataTypeId = "PRIMARY_EMAIL"
            addEmailAddress(pUser, emailAddress)

        }	else {
            println("mail attribute was not found")
        }

        // Processing address
        def address = new Address()
        address.name = "PRIMARY_LOCATION"
        address.address1 = columnMap.get("street")?.value
        address.city = columnMap.get("l")?.value
        address.postalCd = columnMap.get("postalCode")?.value
        address.state = columnMap.get("st")?.value
        address.metadataTypeId = "PRIMARY_LOCATION"
        addAddress(pUser, address)

        println(" - Processing Phone objects: ")

        attrVal = columnMap.get("mobile")
        if (attrVal) {
            def phone = new Phone()
            phone.name = "CELL_PHONE"
            phone.phoneNbr = attrVal.value
            phone.metadataTypeId = "CELL_PHONE"
            addPhone(pUser, phone)
        }

        attrVal = columnMap.get("telephoneNumber")
        if (attrVal) {
            def phone = new Phone()
            phone.name = "DESK_PHONE"
            phone.phoneNbr = attrVal.value
            phone.metadataTypeId = "DESK_PHONE"
            addPhone(pUser, phone)
        }

        attrVal = columnMap.get("facsimileTelephoneNumber")
        if (attrVal) {
            def phone = new Phone()
            phone.name = "FAX"
            phone.phoneNbr = attrVal.value
            phone.metadataTypeId = "FAX"
            addPhone(pUser, phone)
        }

        if (KEEP_AD_ID && isNewUser) {
            println(" - Processing PrincipalName and DN")
            attrVal = columnMap.get(IDENTITY_ATTRIBUTE)
            if (attrVal) {
                // PRE-POPULATE THE USER LOGIN. IN SOME CASES THE COMPANY WANTS TO KEEP THE LOGIN THAT THEY HAVE
                // THIS SHOWS HOW WE CAN DO THAT
                /*  AD primary identity  */
                def lg = new Login()
                lg.operation = AttributeOperationEnum.ADD
                lg.domainId = DOMAIN
                lg.login = attrVal.value
                lg.managedSysId = "0"
                pUser.principalList.add(lg)

                /*  AD target system identity  */
                Login lg2 = new Login()
                lg2.operation = AttributeOperationEnum.ADD
                lg2.domainId = DOMAIN
                lg2.login = attrVal.value
                lg2.managedSysId = AD_MANAGED_SYS_ID
                pUser.principalList.add(lg2)
            }
        }
    }

    def addEmailAddress(ProvisionUser pUser, EmailAddress emailAddress) {
        if (!isNewUser) {
            for (EmailAddress e : pUser.emailAddresses) {
                if (emailAddress.metadataTypeId.equalsIgnoreCase(e.metadataTypeId)) {
                    e.updateEmailAddress(emailAddress)
                    e.setOperation(AttributeOperationEnum.REPLACE)
                    return
                }
            }
        }
        emailAddress.setOperation(AttributeOperationEnum.ADD)
        pUser.emailAddresses.add(emailAddress)
    }

    def addPhone(ProvisionUser pUser, Phone phone) {
        if (!isNewUser) {
            for (Phone p : pUser.phones) {
                if (phone.metadataTypeId.equalsIgnoreCase(p.metadataTypeId)) {
                    p.updatePhone(phone)
                    p.setOperation(AttributeOperationEnum.REPLACE)
                    return
                }
            }
        }
        phone.setOperation(AttributeOperationEnum.ADD)
        pUser.phones.add(phone)
    }

    def addAddress(ProvisionUser pUser, Address address) {
        if (!isNewUser) {
            for (Address a : pUser.addresses) {
                if (address.metadataTypeId.equalsIgnoreCase(a.metadataTypeId)) {
                    a.updateAddress(address)
                    a.setOperation(AttributeOperationEnum.REPLACE)
                    return
                }
            }
        }
        address.setOperation(AttributeOperationEnum.ADD)
        pUser.addresses.add(address)
    }

    def addOrganization(ProvisionUser pUser, String orgName, String orgType) {
        if (!isNewUser) {
            def foundOrg = pUser.affiliations.find { a-> a.organizationName == orgName && a.organizationTypeId == orgType }
            if (foundOrg) {
                return
            }
        }

        def organizationService = context?.getBean("organizationService") as OrganizationService
        def organizationDozerConverter = context?.getBean("organizationDozerConverter") as OrganizationDozerConverter

        def orgSearchBean = new OrganizationSearchBean()
        orgSearchBean.organizationName = orgName
        orgSearchBean.organizationTypeId = orgType
        def orgList = organizationService.findBeans(orgSearchBean, null, 0, 1)
        if (orgList) {
            def organization = organizationDozerConverter?.convertToDTO(orgList.get(0), false)
            organization.operation = AttributeOperationEnum.ADD
            pUser.affiliations.add(organization)
        }
    }

    static def addAttribute(ProvisionUser user, Attribute attr) {
        if (attr?.name) {
            UserAttribute userAttr = new UserAttribute(attr.name, attr.value)
            user.userAttributes.put(attr.name, userAttr)
            println("Attribute '" + attr.name + "' added to the user object.")
        }
    }

    def addRole(ProvisionUser pUser, String roleName) {
        if (!isNewUser) {
            def foundRole = pUser.roles.find { r-> r.roleName == roleName }
            if (foundRole) {
                return
            }
        }
        def roleDataService = context?.getBean("roleDataService") as RoleDataService
        def roleDozerConverter = context?.getBean("roleDozerConverter") as RoleDozerConverter
        def role = roleDozerConverter?.convertToDTO(roleDataService?.getRoleByName(roleName, null), false)
        if (role) {
            role.operation = AttributeOperationEnum.ADD
            pUser.roles.add(role)
        } else {
            println "Role with name " + roleName + " was not found"
        }
    }
}
