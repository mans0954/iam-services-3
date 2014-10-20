/*
 * Created on Jul 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.openiam.exception;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.ws.WebFault;


/**
 * LogoutException is thrown whenever there is an error in the logout
 * process.
 * cause of the exception.
 *
 * @author Suneet Shah
 * @version 1
 */
@WebFault(name = "LogoutException")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogoutException extends Exception {

    public LogoutException(final String message) {
    	super(message);
    }
}
