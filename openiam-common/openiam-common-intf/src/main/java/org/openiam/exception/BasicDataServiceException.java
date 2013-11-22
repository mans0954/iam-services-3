package org.openiam.exception;

import org.openiam.base.ws.ResponseCode;

import java.util.ArrayList;
import java.util.List;

public class BasicDataServiceException extends Exception {

	private ResponseCode code;
	private String responseValue;
	private Throwable originalCause;
    private List<EsbErrorToken> errorTokenList;
	
	public BasicDataServiceException(final ResponseCode code) {
		this.code = code;
	}

    public BasicDataServiceException(final ResponseCode code, EsbErrorToken esbErrorToken) {
        this.code = code;
        this.addErrorToken(esbErrorToken);
    }
	
	public BasicDataServiceException(final ResponseCode code, final Throwable originalCause) {
		this.code = code;
		this.originalCause = originalCause;
	}
	
	public BasicDataServiceException(final ResponseCode code, final String responseValue) {
		this.code = code;
		this.responseValue = responseValue;
	}

	public ResponseCode getCode() {
		return code;
	}

	public void setCode(ResponseCode code) {
		this.code = code;
	}
	
	public String getResponseValue() {
		return responseValue;
	}
	
	public Throwable getOriginalCause() {
		return originalCause;
	}

    public List<EsbErrorToken> getErrorTokenList(){
        return errorTokenList;
    }

    public void addErrorToken(EsbErrorToken errorToken){
        if(errorTokenList==null)
            errorTokenList = new ArrayList<EsbErrorToken>();
        errorTokenList.add(errorToken);
    }
}
