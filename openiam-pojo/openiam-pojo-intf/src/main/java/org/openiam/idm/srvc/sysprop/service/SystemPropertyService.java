package org.openiam.idm.srvc.sysprop.service;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.*;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.sysprop.dto.SystemPropertyDto;
import org.openiam.idm.srvc.sysprop.ws.SystemPropertyWebService;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.UserNoteEntity;
import org.openiam.idm.srvc.user.dto.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Service interface that clients will access to gain information about users
 * and related information.
 *
 * @author Suneet Shah
 * @version 2
 */

public interface SystemPropertyService {
    public List<SystemPropertyDto> getByName(String name);

    public List<SystemPropertyDto> getByType(String mdTypeId);

    public void save(SystemPropertyDto propertyDto);
    public List<SystemPropertyDto> getAll();
    public SystemPropertyDto getById(String id);
}