import org.openiam.idm.srvc.continfo.dto.Address

import java.text.SimpleDateFormat;

import org.openiam.idm.srvc.synch.dto.Attribute;
import org.openiam.idm.srvc.synch.dto.LineObject;
import org.openiam.idm.srvc.synch.service.AbstractTransformScript;
import org.openiam.idm.srvc.synch.service.TransformScript;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.idm.srvc.user.dto.UserStatusEnum
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.org.service.OrganizationDataService;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import java.text.ParseException;

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
        def orgService = orgService()

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
        address.name = "ADDRESS1"
        address.address1 = columnMap.get("STR_1_NM")?.value
        address.address2 = columnMap.get("STR_2_NM")?.value
        address.city = columnMap.get("CITY_NM")?.value
        address.postalCd = columnMap.get("ZIP")?.value
        address.state = columnMap.get("ST_CD")?.value
        address.country = columnMap.get("CTRY_CD")?.value
        addAddress(pUser, address)

        // Processing phone
        def phone = new Phone()
        phone.name = "DESK PHONE"
        phone.phoneNbr = columnMap.get("EMPLOYEE_PHONE_NUMBER")?.value
        addPhone(pUser, phone)

    }

    def addAddress(ProvisionUser pUser, Address address) {
        if (user?.addresses) {
            pUser.addresses = user.addresses
        }
        for (Address a : pUser.addresses) {
            if (address.name.equalsIgnoreCase(a.name)) {
                a.updateAddress(address)
                return
            }
        }
        pUser.addresses.add(address)
    }

    def addPhone(ProvisionUser pUser, Phone phone) {
        if (user?.phones) {
            pUser.phones = user?.phones
        }
        for (Phone p : pUser.phones) {
            if (phone.name.equalsIgnoreCase(p.name)) {
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
