import org.openiam.idm.srvc.continfo.dto.Address

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

if (user?.addresses) {
    pUser.addresses = user.addresses
}
for (Address a : pUser.addresses) {
    if (address.metadataTypeId.equalsIgnoreCase(a.metadataTypeId)) {
        a.updateAddress(address)
        return
    }
}
pUser.addresses.add(address)
output = ""