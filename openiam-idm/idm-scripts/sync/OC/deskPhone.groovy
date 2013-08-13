import org.openiam.idm.srvc.continfo.dto.Phone

def phone = new Phone()
phone.name = "DESK PHONE"
phone.phoneNbr = attribute.value

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
output = ""