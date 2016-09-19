package org.openiam.base.response;

import java.io.Serializable;
import java.util.Set;

public class SetStringResponse implements Serializable {
	
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
