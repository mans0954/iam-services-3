package org.openiam.hazelcast;

import org.openiam.idm.util.CustomJacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.Endpoint;

@RestController
@RequestMapping("/hazelcast")
public class HazelcastRestController {
	
	@Autowired
	private HazelcastConfiguration hazelcastConfig;
	
	@Autowired
	private CustomJacksonMapper mapper;

	@RequestMapping("/instance")
	public @ResponseBody String instance() {
		return hazelcastConfig.getInstance().getName();
	}
	
	@RequestMapping("/cluster")
	public @ResponseBody Cluster cluster() {
		return hazelcastConfig.getInstance().getCluster();
	}
	
	@RequestMapping("/endpoint")
	public @ResponseBody Endpoint endpoint() {
		return hazelcastConfig.getInstance().getLocalEndpoint();
	}
}
