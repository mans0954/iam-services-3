package org.openiam.connector.common.rest;

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
@Component("restConnection")
public class RESTConnectionMgr {

    Connection sqlCon = null;


    private static final Log log = LogFactory.getLog(RESTConnectionMgr.class);

    public RESTConnectionMgr() {
    }

	public HttpURLConnection  connect(ManagedSysDto managedSys,String encrypted) throws   Exception {
    	// REST URL of the form "http://localhost:8080/rest/v1/Users will be appended
        final String url = managedSys.getConnectionString() ;
          HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestProperty("Accept", "application/json");

		connection
				.setRequestProperty(
						"Authorization",
						"Bearer " + encrypted);

		//connection.connect();
		return connection;
	}
}
