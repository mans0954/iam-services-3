package org.openiam.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@Component("httpClientHelper")
public final class OpenIAMHttpClient {
	
	private static final Log LOG = LogFactory.getLog(OpenIAMHttpClient.class);
	
	@Value("${org.openiam.http.client.max.per.route}")
	private int maxNumOfConnectionsPerHost;
	
	@Value("${org.openiam.http.client.max.total}")
	private int maxNumOfTotalConnections;
	
	@Value("${org.openiam.http.client.timeout}")
	private int timeout;
	
	private HttpClient client;
	
	private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
	
	public OpenIAMHttpClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		/*
		SSLContext sslContext = SSLContexts.custom()
		        .loadTrustMaterial(null, new TrustStrategy() {

		            @Override
		            public boolean isTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
		                return true;
		            }
		        })
		        .useTLS()
		        .build();
		*/
		client = HttpClients.custom()
				.setMaxConnPerRoute(maxNumOfConnectionsPerHost)
				.setMaxConnTotal(maxNumOfTotalConnections)
				.setRetryHandler(new DefaultHttpRequestRetryHandler(3, false))
				.setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContexts.custom()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                    .build()
                )).build();
	}
	
	public String doPost(final URL url, final Map<String, String> headers, final Map<String, String> params) throws IOException {
		final RequestConfig config = RequestConfig.custom()
				.setConnectionRequestTimeout(timeout)
				.setSocketTimeout(timeout)
				.build();
		LOG.info(String.format("SMS-PARAMS: %s", params));
		final HttpPost httpPost = new HttpPost(url.toExternalForm());
		httpPost.setConfig(config);
		if(MapUtils.isNotEmpty(headers)) {
			for(final String name : headers.keySet()) {
				final String value = headers.get(name);
				httpPost.addHeader(name, value);
			}
		}
		
		if(MapUtils.isNotEmpty(params)) {
			final List<NameValuePair> postParameters = new ArrayList<>();
			for(final String key : params.keySet()) {
				final String value = params.get(key);
				postParameters.add(new BasicNameValuePair(key, value));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
		}
		final HttpResponse response = client.execute(httpPost);
		if(response == null) {
			return null;
		}
		final int status = response.getStatusLine().getStatusCode();
		final HttpEntity entity = response.getEntity();
		final String content = IOUtils.toString(entity.getContent());
		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Status=%s, Content=%s", status, content));
		}
		IOUtils.closeQuietly(entity.getContent());
		return content;
	}
	
	public String getResponse(final URL url) throws IOException {
		final RequestConfig config = RequestConfig.custom()
													.setConnectionRequestTimeout(timeout)
													.setSocketTimeout(timeout)
													.build();
		final HttpGet httpGet = new HttpGet(url.toExternalForm());
		httpGet.setConfig(config);
		httpGet.addHeader("Accept-Charset", "UTF-8");
		final HttpResponse response = client.execute(httpGet);
		final int status = response.getStatusLine().getStatusCode();
		final HttpEntity entity = response.getEntity();
		InputStream is = null;
		String retval = null;
		try {
			if(status == 200) {
				is = entity.getContent();
			} else {
				throw new IOException(String.format("Response was: %s", status));
			}
		} finally {
			if(is != null) {
				retval = IOUtils.toString(is);
				IOUtils.closeQuietly(is);;
			}
		}
		return retval;
	}
	
	@PostConstruct
	private void init() {
		LOG.info("Initialized HTTP Client");
	}
	
	public HttpClient getClient() {
		return client;
	}
}
