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
package org.openiam.idm.srvc.auth.ws;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.auth.dto.Subject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Response object for a web service operation that returns a role.
 *
 * @author suneet
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthenticationResponse", propOrder = {
        "subject"
})
public class AuthenticationResponse extends Response {

    protected Subject subject;

    public AuthenticationResponse() {
        super();
    }

    public AuthenticationResponse(ResponseStatus s) {
    	super(s);
    }

    /**
     * 
     * @return - the <code>Subject</code>, containing information about the user, and his token
     */
    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    /*
	@Override
	public String toString() {
		return "AuthenticationResponse [subject=" + subject
				+ ", authErrorCode=" + authErrorCode + ", authErrorMessage="
				+ authErrorMessage + ", toString()=" + super.toString() + "]";
	}
	*/
    
}
