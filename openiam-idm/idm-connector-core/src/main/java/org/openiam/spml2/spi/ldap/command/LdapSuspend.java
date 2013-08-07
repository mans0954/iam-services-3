package org.openiam.spml2.spi.ldap.command;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemObjectMatchDAO;
import org.openiam.connector.util.ConnectionMgr;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Implements the suspend functionality for the ldap connector
 * @author suneet
 *
 */
@Deprecated
public class LdapSuspend extends LdapAbstractCommand implements ApplicationContextAware {
	
	private static final Log log = LogFactory.getLog(LdapSuspend.class);

	protected ManagedSystemWebService managedSysService;
	protected ManagedSystemObjectMatchDAO managedSysObjectMatchDao;
	protected LoginDataService loginManager;
	protected SysConfiguration sysConfiguration;

    public static ApplicationContext ac;


	public ResponseType suspend(SuspendResumeRequest request) {
		log.debug("suspend request called..");
		// ldap does not have suspend/disable capability.
		// work around is to scramble the password
        ConnectionMgr conMgr = null;
        ResponseType resp = new ResponseType();
		
//		String requestID = request.getRequestID();
//
//		/* targetID -  */
//		String targetID = request.getTargetID();
//
//
//		/* A) Use the targetID to look up the connection information under managed systems */
//		ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);
//        try {
//
//            log.debug("managedSys found for targetID=" + targetID + " " + " Name=" + managedSys.getName());
//            conMgr = ConnectionFactory.create(ConnectionManagerConstant.LDAP_CONNECTION);
//            conMgr.setApplicationContext(ac);
//
//            LdapContext ldapctx = conMgr.connect(managedSys);
//
//            if (ldapctx == null) {
//                resp.setStatus(StatusCodeType.FAILURE);
//                resp.setError(ErrorCode.DIRECTORY_ERROR);
//                resp.addErrorMessage("Unable to connect to directory.");
//                return resp;
//            }
//
//
//            String ldapName = request.getUserIdentity();
//
//            // check if this object exists in the target system
//            // dont try to disable and object that does not exist
//            if (identityExists(ldapName, ldapctx)) {
//
//                // Each directory
//                Directory dirSpecificImp  = DirectorySpecificImplFactory.create(managedSys.getHandler5());
//
//                log.debug("Directory specific object name = " + dirSpecificImp.getClass().getName());
//
//                ModificationItem[] mods = dirSpecificImp.suspend(request);
//
//                ldapctx.modifyAttributes(ldapName, mods);
//                }
//
//	 	}catch(Exception ne) {
//	 		log.error(ne.getMessage(), ne);
//
//
//	 		resp.setStatus(StatusCodeType.FAILURE);
//	 		resp.setError(ErrorCode.NO_SUCH_IDENTIFIER);
//	 		return resp;
//	 	}finally {
//	 		/* close the connection to the directory */
//	 		  try {
//                  conMgr.close();
//              } catch (NamingException n) {
//                  log.error(n);
//              }
//	 	}
	 	
	 	ResponseType respType = new ResponseType();
	 	respType.setStatus(StatusCodeType.SUCCESS);
	 	return respType;

	}

	public ResponseType resume(SuspendResumeRequest request) {
		log.debug("resume request called..");
        ConnectionMgr conMgr = null;
		// ldap does not have suspend/disable capability.
		// To resume, replace the scrambled password with the one that is stored in the IDM system
		
		String requestID = request.getRequestID();

		/* targetID -  */
		String targetID = request.getTargetID();
		/* ContainerID - May specify the container in which this object should be created
		 *      ie. ou=Development, org=Example */
		//PSOIdentifierType containerID = psoID.getContainerID();
			
	
		/* A) Use the targetID to look up the connection information under managed systems */
//		ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);
//
//        try {
//            log.debug("managedSys found for targetID=" + targetID + " " + " Name=" + managedSys.getName());
//
//            conMgr = ConnectionFactory.create(ConnectionManagerConstant.LDAP_CONNECTION);
//            LdapContext ldapctx = conMgr.connect(managedSys);
//
//            log.debug("Ldapcontext = " + ldapctx);
//            String ldapName = request.getObjectIdentity();
//
//            // check if this object exists in the target system
//            // dont try to enable and object that does not exist
//            if (identityExists(ldapName, ldapctx)) {
//
//                Directory dirSpecificImp  = DirectorySpecificImplFactory.create(managedSys.getHandler5());
//                dirSpecificImp.setAttributes("LDAP_NAME", ldapName);
//                dirSpecificImp.setAttributes("LOGIN_MANAGER", loginManager);
//                dirSpecificImp.setAttributes("CONFIGURATION", sysConfiguration);
//                dirSpecificImp.setAttributes("TARGET_ID",targetID);
//
//                ModificationItem[] mods = dirSpecificImp.resume(request);
//
//                ldapctx.modifyAttributes(ldapName, mods);
//                }
//
//	 	}catch(Exception ne) {
//	 		log.error(ne.getMessage(), ne);
//
//	 		ResponseType resp = new ResponseType();
//	 		resp.setStatus(StatusCodeType.FAILURE);
//	 		resp.setError(ErrorCode.NO_SUCH_IDENTIFIER);
//	 		return resp;
//	 	}finally {
//	 		/* close the connection to the directory */
//	 	    try {
//                  conMgr.close();
//              } catch (NamingException n) {
//                  log.error(n);
//              }
//	 	}
	 	
	 	ResponseType respType = new ResponseType();
	 	respType.setStatus(StatusCodeType.SUCCESS);
	 	return respType;

	}
    public void setApplicationContext(ApplicationContext applicationContext){
        ac = applicationContext;
    }

	public ManagedSystemWebService getManagedSysService() {
		return managedSysService;
	}

	public void setManagedSysService(ManagedSystemWebService managedSysService) {
		this.managedSysService = managedSysService;
	}

	public ManagedSystemObjectMatchDAO getManagedSysObjectMatchDao() {
		return managedSysObjectMatchDao;
	}

	public void setManagedSysObjectMatchDao(
			ManagedSystemObjectMatchDAO managedSysObjectMatchDao) {
		this.managedSysObjectMatchDao = managedSysObjectMatchDao;
	}

	public LoginDataService getLoginManager() {
		return loginManager;
	}

	public void setLoginManager(LoginDataService loginManager) {
		this.loginManager = loginManager;
	}

	public SysConfiguration getSysConfiguration() {
		return sysConfiguration;
	}

	public void setSysConfiguration(SysConfiguration sysConfiguration) {
		this.sysConfiguration = sysConfiguration;
	}
}
