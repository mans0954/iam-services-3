import org.openiam.idm.srvc.continfo.dto.Phone
import org.openiam.idm.srvc.user.service.UserDataService

output = ""

def phone = new Phone()
phone.name = "DESK PHONE"
phone.metadataTypeId = "DESK_PHONE"
phone.phoneNbr = attribute.value

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