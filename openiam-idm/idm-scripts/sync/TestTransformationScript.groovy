import org.openiam.idm.srvc.synch.dto.LineObject
import org.openiam.idm.srvc.synch.service.AbstractTransformScript
import org.openiam.idm.srvc.synch.service.TransformScript
import org.openiam.provision.dto.ProvisionUser
import org.springframework.context.ApplicationContext

class TestTransformationScript extends AbstractTransformScript {

    private ApplicationContext appContext

    @Override
    int execute(LineObject rowObj, ProvisionUser pUser) {
        println "** 1 - Transformation script called."

        println "** 2 - Transformation script completed."

        TransformScript.NO_DELETE
    }

    @Override
    void init() {
    }

    def setAppContext(appContext) {
	    this.appContext = appContext
    }

    def getAppContext() {
	    return appContext
    }

}
