package org.openiam.connector.csv.command.user;

import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.connector.csv.command.base.AbstractCrudCSVCommand;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.idm.parser.csv.CSVParser;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.connector.csv.command.base.AbstractModifyCSVCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("modifyUserCsvCommand")
public class ModifyUserCsvCommand  extends AbstractCrudCSVCommand<ExtensibleUser> {
    @Autowired
    protected CSVParser<ExtensibleUser> extensibleUserCSVParser;

    @Override
    protected void performObjectOperation(String objectIdentity, ExtensibleUser extensibleObject, ManagedSysEntity managedSys) throws ConnectorDataException {
        try {
            List<AttributeMapEntity> attrMapList = managedSysService.getResourceAttributeMaps(managedSys.getResourceId());
            extensibleUserCSVParser.update(new ReconciliationObject<ExtensibleUser>(objectIdentity, extensibleObject), managedSys, attrMapList, CSVSource.IDM);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new  ConnectorDataException(ErrorCode.CSV_ERROR, e.getMessage());
        }
    }
}
