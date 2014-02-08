package org.openiam.idm.groovy.helper

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.idm.srvc.pswd.service.ChallengeResponseService;
import org.openiam.idm.srvc.pswd.ws.PasswordWebService;
import org.openiam.provision.service.ProvisionService;
import org.openiam.idm.srvc.synch.ws.IdentitySynchWebService;
import org.openiam.idm.srvc.synch.ws.AsynchIdentitySynchService;
import org.openiam.idm.srvc.recon.ws.AsynchReconciliationService;
import org.openiam.idm.srvc.recon.ws.ReconciliationWebService;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.idm.srvc.report.ws.ReportWebService;
import org.openiam.script.GroovyScriptEngineIntegration as GSE


class ServiceHelper {
 

	static UserDataWebService userService() {
 	    String BASE_URL = GSE.SERVICE_HOST + GSE.IDM_WS_PATH;

		String serviceUrl = BASE_URL + "/UserDataService"
		String port ="UserDataWebServicePort"
		String nameSpace = "urn:idm.openiam.org/srvc/user/service"
		
		Service service = Service.create(QName.valueOf(serviceUrl))
			
		service.addPort(new QName(nameSpace,port),
				SOAPBinding.SOAP11HTTP_BINDING,	serviceUrl)
		
		return service.getPort(new QName(nameSpace,	port),
				UserDataWebService.class);
	}
	
	static OrganizationDataService orgService() {
        String BASE_URL = GSE.SERVICE_HOST + GSE.IDM_WS_PATH;

		String serviceUrl = BASE_URL + "/OrganizationDataService"
		String port ="OrganizationDataWebServicePort"
		String nameSpace = "urn:idm.openiam.org/srvc/org/service"
		
		Service service = Service.create(QName.valueOf(serviceUrl))
			
		service.addPort(new QName(nameSpace,port),
				SOAPBinding.SOAP11HTTP_BINDING,	serviceUrl)
		
		return service.getPort(new QName(nameSpace,	port),
				OrganizationDataService.class);
	}
	
	static GroupDataWebService groupService() {
        String BASE_URL = GSE.SERVICE_HOST + GSE.IDM_WS_PATH;
	
		String serviceUrl = BASE_URL + "/GroupDataWebService"
		String port ="GroupDataWebServicePort"
		String nameSpace = "urn:idm.openiam.org/srvc/grp/service"
		
		Service service = Service.create(QName.valueOf(serviceUrl))
			
		service.addPort(new QName(nameSpace,port),
				SOAPBinding.SOAP11HTTP_BINDING,	serviceUrl)
		
		return service.getPort(new QName(nameSpace,	port),
				GroupDataWebService.class);
	}
	
	static RoleDataWebService roleService() {
        String BASE_URL = GSE.SERVICE_HOST + GSE.IDM_WS_PATH;
	
		String serviceUrl = BASE_URL + "/RoleDataWebService"
		String port ="RoleDataWebServicePort"
		String nameSpace = "urn:idm.openiam.org/srvc/role/service"
		
		Service service = Service.create(QName.valueOf(serviceUrl))
			
		service.addPort(new QName(nameSpace,port),
				SOAPBinding.SOAP11HTTP_BINDING,	serviceUrl)
		
		return service.getPort(new QName(nameSpace,	port),
				RoleDataWebService.class);
	}
	

	static MailService emailService() {
        String BASE_URL = GSE.SERVICE_HOST + GSE.IDM_WS_PATH;
 
		String serviceUrl = BASE_URL + "/EmailWebService"
		String port ="EmailWebServicePort"
		String nameSpace = "urn:idm.openiam.org/srvc/msg"
		
		Service service = Service.create(QName.valueOf(serviceUrl))
			
		service.addPort(new QName(nameSpace,port),
				SOAPBinding.SOAP11HTTP_BINDING,	serviceUrl)
		
		return service.getPort(new QName(nameSpace,	port),
				MailService.class);
	}

	static LoginDataWebService loginService() {
        String BASE_URL = GSE.SERVICE_HOST + GSE.IDM_WS_PATH;

		String serviceUrl = BASE_URL + "/LoginDataWebService"
		String port ="LoginDataWebServicePort"
		String nameSpace = "urn:idm.openiam.org/srvc/auth/service"
		
		Service service = Service.create(QName.valueOf(serviceUrl))
			
		service.addPort(new QName(nameSpace,port),
				SOAPBinding.SOAP11HTTP_BINDING,	serviceUrl)
		
		return service.getPort(new QName(nameSpace,	port),
				LoginDataWebService.class);
	}

	static PolicyDataService policyService() {
        String BASE_URL = GSE.SERVICE_HOST + GSE.IDM_WS_PATH;
		
		String serviceUrl = BASE_URL + "/PolicyWebService"
		String port ="PolicyWebServicePort"
		String nameSpace = "urn:idm.openiam.org/srvc/policy/service"
		
		Service service = Service.create(QName.valueOf(serviceUrl))
			
		service.addPort(new QName(nameSpace,port),
				SOAPBinding.SOAP11HTTP_BINDING,	serviceUrl)
		
		return service.getPort(new QName(nameSpace,	port),
				PolicyDataService.class);
	}

	static ChallengeResponseService challengeService() {
        String BASE_URL = GSE.SERVICE_HOST + GSE.IDM_WS_PATH;
		
		String serviceUrl = BASE_URL + "/ChallengeResponseWebService"
		String port ="ChallengeResponseWebServicePort"
		String nameSpace = "urn:idm.openiam.org/srvc/pswd/service"
		
		Service service = Service.create(QName.valueOf(serviceUrl))
			
		service.addPort(new QName(nameSpace,port),
				SOAPBinding.SOAP11HTTP_BINDING,	serviceUrl)
		
		return service.getPort(new QName(nameSpace,	port),
				ChallengeResponseService.class);
	}

	static PasswordWebService passwordService() {
        String BASE_URL = GSE.SERVICE_HOST + GSE.IDM_WS_PATH;
		
		String serviceUrl = BASE_URL + "/PasswordWebService"
		String port ="PasswordWebServicePort"
		String nameSpace = "urn:idm.openiam.org/srvc/policy/service"
		
		Service service = Service.create(QName.valueOf(serviceUrl))
			
		service.addPort(new QName(nameSpace,port),
				SOAPBinding.SOAP11HTTP_BINDING,	serviceUrl)
		
		return service.getPort(new QName(nameSpace,	port),
				PasswordWebService.class);
	}

	static ProvisionService povisionService() {
        String BASE_URL = GSE.SERVICE_HOST + GSE.IDM_WS_PATH;
		
		String serviceUrl = BASE_URL + "/ProvisioningService"
		String port ="ProvisionControllerServicePort"
		String nameSpace = "http://www.openiam.org/service/provision"
		
		Service service = Service.create(QName.valueOf(serviceUrl))
			
		service.addPort(new QName(nameSpace,port),
				SOAPBinding.SOAP11HTTP_BINDING,	serviceUrl)
		
		return service.getPort(new QName(nameSpace,	port),
				ProvisionService.class);
	}
		

	static IdentitySynchWebService synchService() {
        String BASE_URL = GSE.SERVICE_HOST + GSE.IDM_WS_PATH;

		String serviceUrl = BASE_URL + "/IdentitySynchWebService"
		String port ="IdentitySynchWebServicePort"
		String nameSpace = "http://www.openiam.org/service/synch"
		
		Service service = Service.create(QName.valueOf(serviceUrl))
			
		service.addPort(new QName(nameSpace,port),
				SOAPBinding.SOAP11HTTP_BINDING,	serviceUrl)
		
		return service.getPort(new QName(nameSpace,	port),
				IdentitySynchWebService.class);
	}
	
	static AsynchIdentitySynchService asynchSynchService() {
        String BASE_URL = GSE.SERVICE_HOST + GSE.IDM_WS_PATH;

		String serviceUrl = BASE_URL + "/Asynch-SynchronizatonService"
		String port ="AsynchIdentitySynchServicePort"
		String nameSpace = "http://www.openiam.org/service/synch"
		
		Service service = Service.create(QName.valueOf(serviceUrl))
			
		service.addPort(new QName(nameSpace,port),
				SOAPBinding.SOAP11HTTP_BINDING,	serviceUrl)
		
		return service.getPort(new QName(nameSpace,	port),
				AsynchIdentitySynchService.class);
	}
	
		static AsynchReconciliationService asyncReconciliationService() {
            String BASE_URL = GSE.SERVICE_HOST + GSE.IDM_WS_PATH;
			
			String serviceUrl = BASE_URL + "/AsynchReconciliationWebService"
			String port ="AsynchReconciliationWebServicePort"
			String nameSpace = "http://www.openiam.org/service/recon"
			
			Service service = Service.create(QName.valueOf(serviceUrl))
				
			service.addPort(new QName(nameSpace,port),
					SOAPBinding.SOAP11HTTP_BINDING,	serviceUrl)
			
			return service.getPort(new QName(nameSpace,	port),
					AsynchReconciliationService.class);
		}
	
		static ReconciliationWebService reconciliationService() {
            String BASE_URL = GSE.SERVICE_HOST + GSE.IDM_WS_PATH;
			
			String serviceUrl = BASE_URL + "/ReconciliationWebService"
			String port ="ReconciliationWebServicePort"
			String nameSpace = "http://www.openiam.org/service/recon"
			
			Service service = Service.create(QName.valueOf(serviceUrl))
				
			service.addPort(new QName(nameSpace,port),
					SOAPBinding.SOAP11HTTP_BINDING,	serviceUrl)
			
			return service.getPort(new QName(nameSpace,	port),
					ReconciliationWebService.class);
		}
	
		static ReportWebService reportService() {
            String BASE_URL = GSE.SERVICE_HOST + GSE.IDM_WS_PATH;
			
			String serviceUrl = BASE_URL + "/ReportWebService"
			String port ="ReportWebServicePort"
			String nameSpace = "urn:idm.openiam.org/srvc/report/service"
			
			Service service = Service.create(QName.valueOf(serviceUrl))
				
			service.addPort(new QName(nameSpace,port),
					SOAPBinding.SOAP11HTTP_BINDING,	serviceUrl)
			
			return service.getPort(new QName(nameSpace,	port),
					ReportWebService.class);
		}
	

	// prov
	// password service
		
	static main(args) {
		
		println("test")
		UserDataWebService userService = ServiceHelper.userService()
		User user = userService.getUserWithDependent ("3006",Boolean.FALSE).getUser();
		println(user.getUserId())
		
		println("test MailService")
		MailService mailSrv = ServiceHelper.emailService();
		mailSrv.send "suneet@gmail.com", "suneet_shah@openiam.com", "test", "I rule"
	
	}

}
