package org.openiam.rest;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.hazelcast.HazelcastConfiguration;
import org.openiam.mq.constants.OAuthAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.constants.URIFederationAPI;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contentprovider")
public class ContentProviderRestController extends AbstractApiService {
	
	@Autowired
    private HazelcastConfiguration hazelcastConfiguration;

	public ContentProviderRestController() {
		super(OpenIAMQueue.RefreshUriFederationCache);
	}

	@RequestMapping("/refresh")
	public @ResponseBody String refresh() {
		publish(URIFederationAPI.RefreshCache, new BaseServiceRequest());
		return "OK";
	}
}
