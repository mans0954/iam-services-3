package org.openiam.rest;

import org.openiam.base.request.EmptyServiceRequest;
import org.openiam.mq.constants.api.OAuthAPI;
import org.openiam.mq.constants.queue.am.RefreshOAuthCache;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauthProviders")
public class OAuthProviderRestController extends AbstractApiService {

	@Autowired
	public OAuthProviderRestController(RefreshOAuthCache queue) {
		super(queue);
	}

	@RequestMapping("/refresh")
	public @ResponseBody String refresh() {
		publish(OAuthAPI.RefreshOAuthCache, new EmptyServiceRequest());
		return "OK";
	}
}
