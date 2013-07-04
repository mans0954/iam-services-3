package org.openiam.spml2.spi.csv.command.user;

import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.idm.parser.csv.CSVParser;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.csv.command.base.AbstractAddCSVCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("addUserCSVCommand")
public class AddUserCSVCommand extends AbstractAddCSVCommand<ProvisionUser> {
    @Autowired
    @Qualifier("userCSVParser")
    protected CSVParser<User> userCSVParser;

    @Override
    protected void addObjectToCsv(String id, ProvisionUser object, ManagedSysEntity managedSys) throws ConnectorDataException {
        try {
            List<AttributeMapEntity> attrMapList = managedSysService.getResourceAttributeMaps(managedSys.getResourceId());
            userCSVParser.add(new ReconciliationObject<User>(id, object.getUser()), managedSys, attrMapList, CSVSource.IDM);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new  ConnectorDataException(ErrorCode.CSV_ERROR, e.getMessage());
        }
    }
}

