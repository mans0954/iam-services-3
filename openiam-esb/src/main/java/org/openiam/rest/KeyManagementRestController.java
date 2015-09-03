package org.openiam.rest;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
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
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	@Value("${iam.jks.path}")
    private String jksFile;
	
	@Autowired
	private KeyManagementService service;
	
	@Autowired
    private HazelcastConfiguration hazelcastConfiguration;

	/**
	 * Convenience method for clietns to re-generate a jks key from the jks distributed cache
	 * Do not Remove!
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/readFromCache")
	public @ResponseBody Response readFromCache() throws IOException {
		final Map<String, byte[]> keyMap = hazelcastConfiguration.getMap("keyManagementCache");
		FileUtils.writeByteArrayToFile(new File(jksFile), keyMap.get("jksFileKey"));
		return new Response().succeed();
	}
	
	/**
	 * Used by chef to initialize key management.  Do not Remove!
	 * @return
	 */
	@RequestMapping("/initKeyManagement")
	public @ResponseBody Response initKeyManagement() {
		return new Response().succeed();
		/*
		TODO:  only run key management if there is no file, and no jks cache key
		final Response response = new Response();
		try {
			service.initKeyManagement();
			response.succeed();
		} catch(Throwable e) {
			log.error("Can't init key management", e);
			response.fail();
		}
		return response;
		*/
	}
}
