package org.openiam.rest;

import org.openiam.base.request.EmptyServiceRequest;
import org.openiam.mq.constants.api.AMCacheAPI;
import org.openiam.mq.constants.queue.am.AMCacheQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authmanager")
public class AuthManagerRestController extends AbstractApiService {
	@Autowired
	public AuthManagerRestController(AMCacheQueue queue)  {
		super(queue);
	}

	@RequestMapping("/refresh")
	public @ResponseBody String refresh() {
		this.publish(AMCacheAPI.RefreshAMManager, new EmptyServiceRequest());
		return "OK";
	}
}
