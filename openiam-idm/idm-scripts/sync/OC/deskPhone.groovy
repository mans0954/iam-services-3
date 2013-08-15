import org.openiam.idm.srvc.continfo.dto.Phone

def phone = new Phone()
phone.name = "DESK PHONE"
phone.metadataTypeId = "DESK_PHONE"
phone.phoneNbr = attribute.value

if (user?.phones) {
    pUser.phones = user?.phones
}
for (Phone p : pUser.phones) {
    if (phone.metadataTypeId.equalsIgnoreCase(p.metadataTypeId)) {
        p.updatePhone(phone)
        return
    }
}
pUser.phones.add(phone)
output = ""