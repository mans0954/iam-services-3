package org.openiam.rest;

import org.openiam.authmanager.service.AuthorizationManagerWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authmanager")
public class AuthManagerRestController {
	
	@Autowired
	protected AuthorizationManagerWebService authorizationManagerServiceClient;

	@RequestMapping("/refresh")
	public @ResponseBody String refresh() {
		authorizationManagerServiceClient.refreshCache();
		return "OK";
	}
}
