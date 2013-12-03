package org.openiam.connector.common.scim;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.springframework.stereotype.Component;


/**
 * Manages connections to SCIM
 * @author Suneet Shah
 *
 */
@Component("scimConnection")
public class SCIMConnectionMgr {

    Connection sqlCon = null;


    private static final Log log = LogFactory.getLog(SCIMConnectionMgr.class);

    public SCIMConnectionMgr() {
    }

	public HttpURLConnection  connect(ManagedSysDto managedSys, String appendToUrl) throws   Exception {
    	// SCIM URL of the form "http://localhost:8080/scim" to which /v1/Users will be appended
        final String url = managedSys.getConnectionString() ;
		HttpURLConnection connection = (HttpURLConnection) new URL(url + appendToUrl).openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestProperty("Accept", "application/json");

		S token = new S();
	    token.setTimestamp(System.currentTimeMillis());
	    String encrypted =token.getPassword();
		//String encrypted = TestRSA.encrypt(token);
		connection
				.setRequestProperty(
						"Authorization",
						"Bearer " + encrypted);

		connection.connect();
		return connection;
	}
}
