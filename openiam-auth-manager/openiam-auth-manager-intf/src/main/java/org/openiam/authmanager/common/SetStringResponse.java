package org.openiam.authmanager.common;

import java.util.Set;

public class SetStringResponse {
	
	public SetStringResponse() {}
	
	public SetStringResponse(final Set<String> stringResp) {
		this.stringResp = stringResp;
	}
	
    private Set<String> stringResp;

    public Set<String> getSetString(){
        return this.stringResp;
    }

    public void setSetString(Set<String> stringResp) {
        this.stringResp = stringResp;
    }
}
