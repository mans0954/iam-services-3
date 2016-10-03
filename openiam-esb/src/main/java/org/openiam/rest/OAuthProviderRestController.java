package org.openiam.rest;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.hazelcast.HazelcastConfiguration;
import org.openiam.mq.constants.OAuthAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauthProviders")
public class OAuthProviderRestController extends AbstractApiService {

	public OAuthProviderRestController() {
		super(OpenIAMQueue.RefreshOAuthCache);
	}

	@RequestMapping("/refresh")
	public @ResponseBody String refresh() {
		publish(OAuthAPI.RefreshOAuthCache, new BaseServiceRequest());
		return "OK";
	}
}
