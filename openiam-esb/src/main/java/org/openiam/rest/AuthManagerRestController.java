package org.openiam.rest;

import org.openiam.hazelcast.HazelcastConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authmanager")
public class AuthManagerRestController {
	
	@Autowired
    private HazelcastConfiguration hazelcastConfiguration;

	@RequestMapping("/refresh")
	public @ResponseBody String refresh() {
		hazelcastConfiguration.getTopic("authManagerTopic").publish("");
		return "OK";
	}
}
