

def postalAddress = null;

if (user.addresses != null && user.addresses.size() > 0) {


 postalAddress =  user.addresses.iterator().next().address1;

if (postalAddress == null || postalAddress.length() == 0) {
	println("postalAddress is null");
	output=null;
}else {
	println("postalAddress =" + postalAddress);
	output = postalAddress
}

}else {
	output = null;
}