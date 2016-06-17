package org.openiam.idm.srvc.sysprop.ws;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.*;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.sysprop.dto.SystemPropertyDto;
import org.openiam.idm.srvc.user.dto.*;
import org.openiam.idm.srvc.user.ws.UserResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.Date;
import java.util.List;

//import org.openiam.idm.srvc.continfo.service.AddressDAO;
//import org.openiam.idm.srvc.continfo.service.EmailAddressDAO;
//import org.openiam.idm.srvc.continfo.service.PhoneDAO;

/**
 * WebService interface that clients will access to gain information about users
 * and related information.
 *
 * @author Suneet Shah
 * @version 2
 */

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/sysprop/ws", name = "SystemPropertyWebService")
public interface SystemPropertyWebService {

    @WebMethod
    public List<SystemPropertyDto> getByName(@WebParam(name = "name", targetNamespace = "") String name);

    @WebMethod
    public List<SystemPropertyDto> getByType(@WebParam(name = "mdTypeId", targetNamespace = "") String mdTypeId);

    @WebMethod
    public void save(@WebParam(name = "propertyDto", targetNamespace = "") SystemPropertyDto propertyDto);

    @WebMethod
    public SystemPropertyDto getById(@WebParam(name = "id", targetNamespace = "") String id);

    public List<SystemPropertyDto> getAll();

}