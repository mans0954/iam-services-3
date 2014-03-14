package org.openiam.http.client;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

public final class OpenIAMHttpClient extends DefaultHttpClient {
	
	private static Logger LOG = Logger.getLogger(OpenIAMHttpClient.class);
	
	public OpenIAMHttpClient(final ClientConnectionManager connectionManager) {
		super(connectionManager);
		init();
	}
	
	private void init() {
		LOG.info("Initialized HTTP Client");
	}
}
