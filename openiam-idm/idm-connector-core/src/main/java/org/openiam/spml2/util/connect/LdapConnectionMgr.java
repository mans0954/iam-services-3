package org.openiam.spml2.util.connect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.util.ConnectionMgr;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import javax.naming.*;
import javax.naming.ldap.*;

import java.util.*;

/**
 * Manages connections to LDAP
 * @author Suneet Shah
 *
 */
public class LdapConnectionMgr implements ConnectionMgr {

	LdapContext ctxLdap = null;
	
    private static final Log log = LogFactory.getLog(LdapConnectionMgr.class);
    public static ApplicationContext ac;

    @Value("${KEYSTORE}")
    private String keystore;

    public LdapConnectionMgr() {
    	
    }
    


	public LdapContext connect(ManagedSysDto managedSys)  throws NamingException{

		LdapContext ldapContext = null;
		Hashtable<String, String> envDC = new Hashtable();
	
        if (keystore != null && !keystore.isEmpty())  {
		    System.setProperty("javax.net.ssl.trustStore",keystore);
        }

        if (managedSys == null) {
            log.debug("ManagedSys is null");
            return null;
        }

		String hostUrl = managedSys.getHostUrl();
		if (managedSys.getPort() > 0 ) {
			hostUrl = hostUrl + ":" + String.valueOf(managedSys.getPort());
		}

        log.debug("connect: Connecting to target system: " + managedSys.getManagedSysId() );
        log.debug("connect: Managed System object : " + managedSys);

		//log.info(" directory login = " + managedSys.getUserId() );
		//log.info(" directory login passowrd= " + managedSys.getDecryptPassword() );
		
		envDC.put(Context.PROVIDER_URL,hostUrl);
		envDC.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");		
		envDC.put(Context.SECURITY_AUTHENTICATION, "simple" ); // simple
		envDC.put(Context.SECURITY_PRINCIPAL,managedSys.getUserId());  //"administrator@diamelle.local"
		envDC.put(Context.SECURITY_CREDENTIALS,managedSys.getPswd());

        /*
        Protocol is defined in the url - ldaps vs ldap
        This is not necessary
        if (managedSys.getCommProtocol() != null && managedSys.getCommProtocol().equalsIgnoreCase("SSL")) {
			envDC.put(Context.SECURITY_PROTOCOL, managedSys.getCommProtocol());
		}
		*/


        try {

            ldapContext = new InitialLdapContext(envDC,null);

        }catch (CommunicationException ce) {
            // check if there is a secondary connection linked to this
            String secondarySysID =  managedSys.getSecondaryRepositoryId();

            log.debug("Secondary Sys ID is " + secondarySysID);

            if (secondarySysID != null && !secondarySysID.isEmpty()) {

                // recursively search through the chained list of linked managed systems
                ManagedSystemService managedSysService =  (ManagedSystemService) ac.getBean("managedSystemService");
                ManagedSysEntity secondarySys =  managedSysService.getManagedSysById(secondarySysID);
                return connect(secondarySys);

            }
            // no secondary repository
            log.debug("Throw communication exception." + ce.toString());
            throw ce;


        } catch(NamingException ne) {
            log.error(ne.toString());
            throw ne;

        }catch (Exception e) {
            log.error(e.toString());
            return null;
        }

		return ldapContext;

	}



	public void close() throws NamingException {

		if (this.ctxLdap != null) { 
    		ctxLdap.close();
		}
		ctxLdap = null;
		
	}

    public void setApplicationContext(ApplicationContext applicationContext){
        ac = applicationContext;
    }

}
