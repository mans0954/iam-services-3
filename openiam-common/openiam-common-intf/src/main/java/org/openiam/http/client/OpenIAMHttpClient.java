package org.openiam.http.client;

import javax.annotation.PostConstruct;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
