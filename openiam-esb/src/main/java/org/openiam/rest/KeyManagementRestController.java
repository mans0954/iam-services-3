package org.openiam.rest;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openiam.hazelcast.HazelcastConfiguration;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/keymanagement")
public class KeyManagementRestController {
	
	@Value("${iam.jks.path}")
    private String jksFile;
	
	@Autowired
	private KeyManagementService service;
	
	@Autowired
    private HazelcastConfiguration hazelcastConfiguration;

	@RequestMapping("/readFromCache")
	public @ResponseBody String readFromCache() throws IOException {
		final Map<String, byte[]> keyMap = hazelcastConfiguration.getMap("keyManagementCache");
		FileUtils.writeByteArrayToFile(new File(jksFile), keyMap.get("jksFileKey"));
		return "OK";
	}
}
