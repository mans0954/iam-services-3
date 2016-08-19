package org.openiam.http.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.auth.AuthenticationException;
import org.testng.annotations.Test;

public class TestOpeniamHttpClient {

	@Test
	public void testSelfSignedCertificate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, AuthenticationException, MalformedURLException, IOException {
		final OpenIAMHttpClient client = new OpenIAMHttpClient();
		client.doPost(new URL("https://srvc.smsglobal.com/v1/sms/"), null, null, null);
	}
}
