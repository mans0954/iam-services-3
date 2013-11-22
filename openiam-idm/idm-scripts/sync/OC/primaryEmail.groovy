import org.openiam.idm.srvc.continfo.dto.EmailAddress
import org.openiam.idm.srvc.user.service.UserDataService

output = ""

def emailAddress = new EmailAddress()
emailAddress.name = "PRIMARY_EMAIL"
emailAddress.isDefault = true
emailAddress.isActive = true
emailAddress.emailAddress = attribute.value
emailAddress.metadataTypeId = "PRIMARY_EMAIL"

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