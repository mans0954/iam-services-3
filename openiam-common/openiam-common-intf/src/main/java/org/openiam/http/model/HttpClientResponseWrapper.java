package org.openiam.http.model;

public class HttpClientResponseWrapper {

	private int status;
	private String response;
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	
	public HttpClientResponseWrapper(final int status, final String response) {
		this.status = status;
		this.response = response;
	}
}
