package org.openiam.rest;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.hazelcast.HazelcastConfiguration;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/keymanagement")
public class KeyManagementRestController extends AbstractBaseService {
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	@Value("${iam.jks.path}")
    private String jksFile;
	
	@Autowired
	private KeyManagementService service;
	
	@Autowired
    private HazelcastConfiguration hazelcastConfiguration;

	/**
	 * Convenience method for clients to re-generate a jks key from the jks distributed cache
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
	 * @return this method is used by shell scripts, which don't easily parse json.  Therefore, any error will
	 * return an actual 500.
	 * If you change this functionality, change the underlying chef recipe too!!!
	 * @throws Exception 
	 */
	@RequestMapping("/initKeyManagement")
	public void initKeyManagement() throws Exception { 
		if(!hasKeyManagementToolBeenRun()) {
			service.initKeyManagement();
		} else {
			throw new RuntimeException("Key Management tool has been run before - not initializing.  Don't worry, this is a normal error.  A shell script is probably calling this method.");
		}
	}
	
	private boolean hasKeyManagementToolBeenRun() {
		final AuditLogSearchBean searchBean = new AuditLogSearchBean();
		searchBean.setAction(AuditAction.KEY_MANAGEMENT_INITIALIZATION.value());
		return CollectionUtils.isNotEmpty(auditLogService.findIDs(searchBean, 0, 1));
	}
	
	/**
	 * Convenience method to re-genrate a jks key
	 */
	@RequestMapping("/recalculateKey")
	public @ResponseBody Response recalculateKey() { 
		final Response response = new Response();
		try {
			service.initKeyManagement();
			response.succeed();
		} catch(Throwable e) {
			log.error("Can't init key management", e);
			response.fail();
		}
		return response;
	}
}
