package org.openiam.connector.util.connect;

import com.sun.jndi.ldap.LdapCtxFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.util.ConnectionMgr;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.naming.*;
import javax.naming.ldap.*;

import java.util.*;

/**
 * Manages connections to LDAP
 * @author Suneet Shah
 *
 */
@Service
public class LdapConnectionMgr implements ConnectionMgr {

    @Autowired
    @Qualifier("cryptor")
    private Cryptor cryptor;
    @Autowired
    private KeyManagementService keyManagementService;

    private static final Log log = LogFactory.getLog(LdapConnectionMgr.class);
    public static ApplicationContext ac;

    @Value("${KEYSTORE}")
    private String keystore;
    @Value("${KEYSTORE_PSWD}")
    private String keystorePasswd;

    @Value("${org.openiam.idm.system.user.id}")
    private String systemUserId;

    private LdapCtxFactory ldapCtxFactory;

    public LdapConnectionMgr() {
        ldapCtxFactory = new LdapCtxFactory();

    }

    protected String getDecryptedPassword(ManagedSysEntity managedSys) throws ConnectorDataException {
        String result = null;
        if( managedSys.getPswd()!=null){
            try {
                result = cryptor.decrypt(keyManagementService.getUserKey(systemUserId, KeyName.password.name()), managedSys.getPswd());
            } catch (Exception e) {
                log.error(e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
            }
        }
        return result;
    }

	public LdapContext connect(ManagedSysEntity managedSys) throws NamingException {

        LdapContext ldapContext = null;
		Hashtable<String, String> envDC = new Hashtable();
	
        if (keystore != null && !keystore.isEmpty())  {
		    System.setProperty("javax.net.ssl.trustStore", keystore);
            System.setProperty("javax.net.ssl.keyStorePassword", keystorePasswd);
        }

        if (managedSys == null) {
            log.debug("ManagedSys is null");
            return null;
        }

		String hostUrl = managedSys.getHostUrl();
		if (managedSys.getPort() > 0 ) {
			hostUrl = hostUrl + ":" + String.valueOf(managedSys.getPort());
		}
        String decryptedPassword;
        try {
            decryptedPassword = getDecryptedPassword(managedSys);
        } catch (ConnectorDataException e) {
            decryptedPassword = managedSys.getPswd();
            log.error("connect", e);
        }
        log.debug("connect: Connecting to target system: " + managedSys.getId() );
        log.debug("connect: Managed System object : " + managedSys);

		log.info(" directory login = " + managedSys.getUserId() );
		log.info(" directory login passwrd= *****" ); //IDMAPPS-1846
        log.info(" javax.net.ssl.trustStore= " + System.getProperty("javax.net.ssl.trustStore"));
        log.info(" javax.net.ssl.keyStorePassword= " + System.getProperty("javax.net.ssl.keyStorePassword"));

		envDC.put(Context.PROVIDER_URL,hostUrl);
		envDC.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");		
		envDC.put(Context.SECURITY_AUTHENTICATION, "simple" ); // simple
		envDC.put(Context.SECURITY_PRINCIPAL,managedSys.getUserId() != null ? managedSys.getUserId() : "");  //"administrator@diamelle.local"
		envDC.put(Context.SECURITY_CREDENTIALS,decryptedPassword);

        //Connections Pool configuration
        envDC.put("com.sun.jndi.ldap.connect.pool", "true");
        // Here is an example of a command line that sets the maximum pool size to 20, the preferred pool size to 10, and the idle timeout to 5 minutes for pooled connections.
        envDC.put("com.sun.jndi.ldap.connect.pool.prefsize", "10");
        envDC.put("com.sun.jndi.ldap.connect.pool.maxsize", "20");
        envDC.put("com.sun.jndi.ldap.connect.pool.timeout", "300000");
        /*
        Protocol is defined in the url - ldaps vs ldap
        This is not necessary
        if (managedSys.getCommProtocol() != null && managedSys.getCommProtocol().equalsIgnoreCase("SSL")) {
			envDC.put(Context.SECURITY_PROTOCOL, managedSys.getCommProtocol());
		}
		*/
       // if (managedSys.getCommProtocol() != null && managedSys.getCommProtocol().equalsIgnoreCase("SSL")) {
        //    envDC.put(Context.SECURITY_PROTOCOL, "SSL");
        //}

        try {
            ldapContext = (LdapContext) ldapCtxFactory.getInitialContext((Hashtable) envDC);

        } catch (CommunicationException ce) {
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
            log.debug("Throw communication exception.", ce);
            throw ce;


        } catch(NamingException ne) {
            log.error(ne.toString(), ne);
            throw ne;

        } catch (Throwable e) {
            log.error(e.toString(), e);
            return null;
        }

		return ldapContext;

	}


    public void close() throws NamingException {
       //Not implemented yet
	}

    public void setApplicationContext(ApplicationContext applicationContext){
        ac = applicationContext;
    }

}
