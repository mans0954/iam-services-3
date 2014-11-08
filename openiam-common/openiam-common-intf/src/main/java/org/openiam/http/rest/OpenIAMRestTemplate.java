package org.openiam.http.rest;


import javax.annotation.PostConstruct;

import org.openiam.http.client.OpenIAMHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("restTemplateInitializer")
public class OpenIAMRestTemplate {
	
	@Autowired
	private RestTemplate template;
	
	@Autowired
	private OpenIAMHttpClient client;
	
	@PostConstruct
	public void init() {
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client.getClient()));
	}
}
