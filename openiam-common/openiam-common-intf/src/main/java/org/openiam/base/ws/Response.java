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
import java.util.List;

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
        "errorTokenList"
})
public class Response implements Serializable {

    @XmlAttribute(required = true)
    protected ResponseStatus status;
    protected ResponseCode errorCode;
    protected String errorText;

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

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public Object getResponseValue() {
        return responseValue;
    }

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
        return status.equals(ResponseStatus.SUCCESS);
    }

    public boolean isFailure() {
        return status.equals(ResponseStatus.FAILURE);
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

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", errorCode=" + errorCode +
                ", errorText='" + errorText + '\'' +
                ", responseValue=" + responseValue +
                ", errorTokenList=" + errorTokenList +
                '}';
    }
}
