package org.openiam.rest;

import org.openiam.base.request.EmptyServiceRequest;
import org.openiam.hazelcast.HazelcastConfiguration;
import org.openiam.mq.constants.api.URIFederationAPI;
import org.openiam.mq.constants.queue.am.RefreshUriFederationCache;
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

	@Autowired
	public ContentProviderRestController(RefreshUriFederationCache queue) {
		super(queue);
	}

	@RequestMapping("/refresh")
	public @ResponseBody String refresh() {
		publish(URIFederationAPI.RefreshCache, new EmptyServiceRequest());
		return "OK";
	}
}
