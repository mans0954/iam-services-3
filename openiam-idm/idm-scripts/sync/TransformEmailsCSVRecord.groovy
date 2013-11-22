import org.openiam.idm.srvc.continfo.dto.EmailAddress
import org.openiam.idm.srvc.synch.dto.LineObject
import org.openiam.idm.srvc.synch.service.AbstractTransformScript
import org.openiam.idm.srvc.synch.service.TransformScript
import org.openiam.idm.srvc.user.service.UserDataService
import org.openiam.provision.dto.ProvisionUser

class TransformEmailsCSVRecord extends AbstractTransformScript {

    @Override
    void init() {}

    @Override
    public int execute(LineObject rowObj, ProvisionUser pUser) {

        def columnMap =  rowObj.columnMap

        // Processing email address
        def emailAddress = new EmailAddress()
        emailAddress.name = "PRIMARY_EMAIL"
        emailAddress.isDefault = true
        emailAddress.isActive = true
        emailAddress.emailAddress = columnMap.get("EMAIL")?.value
        emailAddress.metadataTypeId = "PRIMARY_EMAIL"
        addEmailAddress(pUser, emailAddress)

        return TransformScript.NO_DELETE
    }

    def addEmailAddress(ProvisionUser pUser, EmailAddress emailAddress) {
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

}
