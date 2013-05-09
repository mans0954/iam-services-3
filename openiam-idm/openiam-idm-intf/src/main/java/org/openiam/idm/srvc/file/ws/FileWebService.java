package org.openiam.idm.srvc.file.ws;

import java.io.File;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.springframework.stereotype.Service;

/**
 * @author zaporozhec
 */
@WebService
public interface FileWebService {

	@WebMethod
	String getFile(@WebParam(name = "fName", targetNamespace = "") String fName);

	@WebMethod
	File saveFile(@WebParam(name = "fName", targetNamespace = "") String fName,
			@WebParam(name = "value", targetNamespace = "") String value);

}