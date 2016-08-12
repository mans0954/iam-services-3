package org.openiam.rest;

import org.openiam.base.ws.Response;
import org.openiam.base.request.AuthenticationRequest;
import org.openiam.idm.srvc.auth.service.AuthenticationServiceService;
import org.openiam.base.response.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * Used by the OpenIAM proxy for Kerberos and Cert authentication
 * 
 * Do *not* modify without talking to the entire team
 * 
 * @author Lev Bornovalov
 *
 */
@RestController
@RequestMapping("/auth/")
public class AuthenticationRestController {
	
	@Autowired
	private AuthenticationServiceService authenticationService;

	@RequestMapping(value="/login", method=RequestMethod.POST)
	public @ResponseBody AuthenticationResponse login(final @RequestBody AuthenticationRequest request) {
		return authenticationService.login(request);
	}
	
	@RequestMapping(value="/renewToken", method=RequestMethod.GET)
	public @ResponseBody Response renewToken(final @RequestParam(value="principal", required=true) String principal, 
											 final @RequestParam(value="token", required=true) String token, 
											 final @RequestParam(value="tokenType", required=true) String tokenType, 
											 final @RequestParam(value="patternId", required=true) String patternId) {
		return authenticationService.renewToken(principal, token, tokenType, patternId);
	}
}
