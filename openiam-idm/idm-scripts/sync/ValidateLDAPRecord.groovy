import org.openiam.idm.srvc.synch.dto.LineObject
import org.openiam.idm.srvc.synch.service.ValidationScript

public class ValidateLDAPRecord implements ValidationScript {
	
	public int isValid(LineObject rowObj) {
        println "** 1 - Validation script called."

        println "** 2 - Validation script completed and is valid."

        ValidationScript.VALID
	}
	
}
