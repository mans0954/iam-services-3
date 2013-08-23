import org.openiam.idm.srvc.continfo.dto.Address
import org.openiam.idm.srvc.user.service.UserDataService

output = ""

def columnMap =  rowObj.columnMap

def address = new Address()
address.name = attributeName
address.address1 = columnMap.get("STR_1_NM")?.value
address.address2 = columnMap.get("STR_2_NM")?.value
address.city = columnMap.get("CITY_NM")?.value
address.postalCd = columnMap.get("ZIP")?.value
address.state = columnMap.get("ST_CD")?.value
address.country = columnMap.get("CTRY_CD")?.value
address.metadataTypeId = attributeName

def addresses = []
if (!isNewUser) {
    def userManager = context?.getBean("userManager") as UserDataService
    addresses = userManager.getAddressDtoList(user.userId, false)
}
if (addresses) {
    pUser.addresses = addresses
}
for (Address a : pUser.addresses) {
    if (address.metadataTypeId.equalsIgnoreCase(a.metadataTypeId)) {
        a.updateAddress(address)
        return
    }
}
pUser.addresses.add(address)