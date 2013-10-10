
if (user.addresses != null && user.addresses.size() > 0) {
	List<String> addresses = new LinkedList<String>();
    for(org.openiam.idm.srvc.continfo.dto.Address address :  user.addresses) {
        StringBuilder addressString = new StringBuilder();
        if (org.apache.commons.lang.StringUtils.isNotEmpty(address.getAddress1())) {
            addressString.append(address.getAddress1());
            addressString.append(", ");
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(address.getAddress2())) {
            addressString.append(address.getAddress2());
            addressString.append(", ");
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(address.getAddress3())) {
            addressString.append(address.getAddress3());
            addressString.append(", ");
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(address.getAddress4())) {
            addressString.append(address.getAddress4());
            addressString.append(", ");
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(address.getAddress5())) {
            addressString.append(address.getAddress5());
            addressString.append(", ");
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(address.getAddress6())) {
            addressString.append(address.getAddress6());
            addressString.append(", ");
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(address.getAddress7())) {
            addressString.append(address.getAddress7());
        }
        addresses.add(addressString.toString());
    }

    output=addresses

} else {
    output=null
}
