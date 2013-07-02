import org.openiam.idm.srvc.auth.dto.Login

// Use this script if column name matches pUser property name
if (objectType == "USER") {
    pUser."${attribute.name}" = attribute.value

} else if (objectType == "PRINCIPAL") {
    if (isNewUser) {

        def l = new Login()
        l.domainId = "USR_SEC_DOMAIN"
        l.login = attribute.value
        l.managedSysId = "0"

        principalList.add(l)
    }
    if (principalList) {
        pUser.principalList = principalList
    }
}
output = ""