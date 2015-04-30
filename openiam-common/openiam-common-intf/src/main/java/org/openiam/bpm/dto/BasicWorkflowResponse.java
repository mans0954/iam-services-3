package org.openiam.bpm.dto;

import org.openiam.base.ws.ResponseStatus;

public class BasicWorkflowResponse extends AbstractWorkflowResponse {

	public BasicWorkflowResponse() {}
	
	public BasicWorkflowResponse(final ResponseStatus code) {
		super(code);
	}
}
