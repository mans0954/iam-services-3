/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 *
 */
package org.openiam.base.ws;


import org.openiam.exception.EsbErrorToken;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class that is used for responses from a web service.
 *
 * @author Suneet Shah
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Response", propOrder = {
        "status",
        "errorCode",
        "responseValue",
        "errorText",
        "errorTokenList",
        "fieldMappings",
        "stacktraceText"
})
public class Response implements Serializable {

    @XmlAttribute(required = true)
    protected ResponseStatus status;
    protected ResponseCode errorCode;
    protected String errorText;
    protected Map<String, String> fieldMappings;
    private String stacktraceText;
    /**
     * Use inherited classes to return expected value to the caller
     */
    @Deprecated
    protected Object responseValue;
    private List<EsbErrorToken> errorTokenList;

    public Response() {
    }

    public Response(ResponseStatus s) {
        status = s;
    }

    public ResponseStatus getStatus() {
        return status;
    }
    
    public Response succeed() {
    	this.status = ResponseStatus.SUCCESS;
    	return this;
    }
    
    public Response fail() {
    	this.status = ResponseStatus.FAILURE;
    	return this;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }
    /**
     * Use inherited classes to return expected value to the caller
     */
    @Deprecated
    public Object getResponseValue() {
        return responseValue;
    }
    /**
     * Use inherited classes to return expected value to the caller
     */
    @Deprecated
    public void setResponseValue(Object responseValue) {
        this.responseValue = responseValue;
    }

    public ResponseCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ResponseCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public boolean isSuccess() {
    	return ResponseStatus.SUCCESS.equals(status);
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    public List<EsbErrorToken> getErrorTokenList() {
        return errorTokenList;
    }

    public void setErrorTokenList(List<EsbErrorToken> errorTokenList) {
        this.errorTokenList = errorTokenList;
    }

    public void addErrorToken(EsbErrorToken errorToken){
        if(errorTokenList==null)
            errorTokenList = new ArrayList<EsbErrorToken>();
        errorTokenList.add(errorToken);
    }
    
    public String getFieldMapping(final String field) {
    	return (field != null && fieldMappings != null) ? fieldMappings.get(field) : null;
    }
    
    public void addFieldMapping(final String field, final String value) {
    	if(field != null && value != null) {
    		if(this.fieldMappings == null) {
    			this.fieldMappings = new HashMap<>();
    		}
    		this.fieldMappings.put(field, value);
    	}
    }

    public Map<String, String> getFieldMappings() {
		return fieldMappings;
	}

	public void setFieldMappings(Map<String, String> fieldMappings) {
		this.fieldMappings = fieldMappings;
	}
	
	

	public String getStacktraceText() {
		return stacktraceText;
	}

	public void setStacktraceText(String stacktraceText) {
		this.stacktraceText = stacktraceText;
	}

	@Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", errorCode=" + errorCode +
                ", errorText='" + errorText + '\'' +
                ", responseValue=" + responseValue +
                ", errorTokenList=" + errorTokenList +
                ", stacktraceText=" + stacktraceText +
                '}';
    }

    public Response convertToBase(){
        Response response = new Response();
        response.setResponseValue(this.getValueInternal());
        response.setErrorCode(this.errorCode);
        response.setErrorText(this.errorText);
        response.setErrorTokenList(this.getErrorTokenList());
        response.setFieldMappings(this.getFieldMappings());
        response.setStacktraceText(this.getStacktraceText());
        response.setStatus(this.getStatus());
        return response;
    }

    protected Object getValueInternal(){
        return this.responseValue;
    }
}
