package org.openiam.rest;

import org.openiam.am.srvc.ws.URIFederationWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contentprovider")
public class ContentProviderRestController {
	
	@Autowired
    private URIFederationWebService uriFederationService;

	@RequestMapping("/refresh")
	public @ResponseBody String refresh() {
		uriFederationService.sweep();
		return "OK";
	}
}
