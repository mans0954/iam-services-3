package org.openiam.test.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.springframework.stereotype.Component;

public class SoapHeaderInterceptor extends AbstractSoapInterceptor {

	public SoapHeaderInterceptor() {
        super(Phase.POST_LOGICAL);
    }

	public SoapHeaderInterceptor(String p) {
		super(p);
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		// Get request HTTP headers
	    //Map<String, List<String>> inHeaders = (Map<String, List<String>>) message.getExchange().getInMessage().get(Message.PROTOCOL_HEADERS);
	    // Get response HTTP headers
	    Map<String, List<String>> outHeaders = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);
	    if (outHeaders == null) {
	        outHeaders = new HashMap<>();
	        message.put(Message.PROTOCOL_HEADERS, outHeaders);
	    }
	    // Copy Custom HTTP header on the response
	    outHeaders.put("x-openiam-userId", Arrays.asList(new String[] {getUserId()}));
	    outHeaders.put("x-openiam-principal", Arrays.asList(new String[] {getPrincipal()}));
	    outHeaders.put("x-openiam-language-id", Arrays.asList(new String[] {"1"}));
	}

	private String getUserId() {
		if(IdentityHolder.getInstance().getUserId() != null) {
			return IdentityHolder.getInstance().getUserId();
		} else {
			return "3000";
		}
	}
	
	private String getPrincipal() {
		if(IdentityHolder.getInstance().getPrincipal() != null) {
			return IdentityHolder.getInstance().getPrincipal();
		} else {
			return "sysadmin";
		}
	}
}
