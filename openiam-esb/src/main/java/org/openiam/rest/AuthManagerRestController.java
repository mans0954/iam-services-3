package org.openiam.rest;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.hazelcast.HazelcastConfiguration;
import org.openiam.mq.constants.AMManagerAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authmanager")
public class AuthManagerRestController extends AbstractApiService {
	
	@Autowired
    private HazelcastConfiguration hazelcastConfiguration;

	public AuthManagerRestController() {
		super(OpenIAMQueue.AMManagerQueue);
	}

	@RequestMapping("/refresh")
	public @ResponseBody String refresh() {
		this.sendAsync(AMManagerAPI.RefreshCache, new BaseServiceRequest());
//		hazelcastConfiguration.getTopic("authManagerTopic").publish("");
		return "OK";
	}
}
