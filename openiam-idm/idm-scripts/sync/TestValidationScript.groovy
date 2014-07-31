import org.openiam.idm.srvc.synch.dto.LineObject
import org.openiam.idm.srvc.synch.service.AbstractValidationScript
import org.openiam.idm.srvc.synch.service.ValidationScript
import org.springframework.context.ApplicationContext

class TestValidationScript extends AbstractValidationScript {

    public int isValid(LineObject rowObj) {
        println "** 1 - Validation script called."

        println "** 2 - Validation script completed and is valid."

        ValidationScript.VALID
    }

}
