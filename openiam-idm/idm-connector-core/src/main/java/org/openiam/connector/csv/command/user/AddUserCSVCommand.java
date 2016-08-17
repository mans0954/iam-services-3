package org.openiam.connector.csv.command.user;

import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.connector.csv.command.base.AbstractCrudCSVCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.idm.parser.csv.CSVParser;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service("addUserCSVCommand")
public class AddUserCSVCommand extends AbstractCrudCSVCommand<ExtensibleUser> {
    @Autowired
    @Qualifier("userCSVParser")
    protected CSVParser<User> userCSVParser;

    @Autowired
    @Qualifier("userManager")
    protected UserDataService userManager;

    @Override
    protected void performObjectOperation(String principal, ExtensibleUser object, ManagedSysEntity managedSys) throws ConnectorDataException {
        try {
            List<AttributeMapEntity> attrMapList = (managedSys.getResource() != null) ? managedSysService.getResourceAttributeMaps(managedSys.getResource().getId()) : Collections.EMPTY_LIST;
            //TODO check
            User user = userManager.getUserDtoByPrincipal(principal,managedSys.getId(),true);
            userCSVParser.add(new ReconciliationObject<User>(principal, user), managedSys, attrMapList, CSVSource.IDM);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new  ConnectorDataException(ErrorCode.CSV_ERROR, e.getMessage());
        }
    }
}

