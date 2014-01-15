/**
 * bindingMap contains following attributes:
 * @objectType
 * @policy
 * @rowObj
 * @attributeName
 * @attribute
 * @pUser
 * @user
 * @principalList
 * @userRoleList
 * @isNewUser
 */

import org.openiam.idm.srvc.auth.dto.Login

// Use this script if column name matches pUser property name
if (objectType == "USER") {
    try {
        pUser."${attributeName}" = attribute?.value
    } catch (Exception e) {
        println("Failed to set provision user property " + attributeName + ": " + e.getMessage())
    }

} else if (objectType == "PRINCIPAL") {
    if (isNewUser) {

        def l = new Login()
        l.login = attribute.value
        l.managedSysId = "0"

        principalList.add(l)
    }
    if (principalList) {
        pUser.principalList = principalList
    }
}
output = ""