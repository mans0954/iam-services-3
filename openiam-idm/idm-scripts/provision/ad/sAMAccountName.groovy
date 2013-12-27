
def loginManager = context.getBean("loginManager")

ctr = 1;

loginID=user.firstName + "." + user.lastName



if (loginID.length() > 17) {
	loginID = loginID.substring(0,17);
	
	// add logic to ensure uniqueness
	
	if (managedSysId != null) {
		
		origLoginID = loginId;
		
		while ( loginManager.loginExists( loginID, managedSysId )) {
		  strCtrSize = String.valueOf(ctr)
			loginId=   origLoginID + ctr;
			ctr++
		}

	}


}


output=loginID


