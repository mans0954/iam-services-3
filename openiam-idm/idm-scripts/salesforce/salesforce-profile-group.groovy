if(user.getAttribute("SalesForceProfileName") == null) {
	output = null;
} else {
	output = user.getAttribute("SalesForceProfileName").getValue();
}