package org.openiam.rest;

import org.openiam.base.request.RenewTokenRequest;
import org.openiam.base.response.SSOTokenResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.request.AuthenticationRequest;
import org.openiam.idm.srvc.auth.service.AuthenticationServiceService;
import org.openiam.base.response.AuthenticationResponse;
import org.openiam.mq.constants.AuthenticationAPI;
import org.openiam.mq.constants.queue.am.AuthenticationQueue;
import org.openiam.srvc.AbstractApiService;
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
public class AuthenticationRestController extends AbstractApiService {

	@Autowired
	private AuthenticationServiceService authenticationService;
	@Autowired
	public AuthenticationRestController(AuthenticationQueue queue) {
		super(queue);
	}

	@RequestMapping(value="/login", method=RequestMethod.POST)
	public @ResponseBody AuthenticationResponse login(final @RequestBody AuthenticationRequest request) {
		return this.manageApiRequest(AuthenticationAPI.Authenticate, request, AuthenticationResponse.class);
	}
	
	@RequestMapping(value="/renewToken", method=RequestMethod.GET)
	public @ResponseBody Response renewToken(final @RequestParam(value="principal", required=true) String principal, 
											 final @RequestParam(value="token", required=true) String token, 
											 final @RequestParam(value="tokenType", required=true) String tokenType, 
											 final @RequestParam(value="patternId", required=true) String patternId) {
		RenewTokenRequest request = new RenewTokenRequest();
		request.setPrincipal(principal);
		request.setToken(token);
		request.setTokenType(tokenType);
		request.setPatternId(patternId);

		SSOTokenResponse response = this.manageApiRequest(AuthenticationAPI.RenewToken, request, SSOTokenResponse.class);
		return response.convertToBase();
	}
}
