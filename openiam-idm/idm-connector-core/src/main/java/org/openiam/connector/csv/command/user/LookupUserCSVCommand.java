package org.openiam.connector.csv.command.user;

import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.idm.parser.csv.CSVParser;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultField;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.connector.csv.command.base.AbstractLookupCSVCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("lookupUserCSVCommand")
public class LookupUserCSVCommand extends AbstractLookupCSVCommand<User, ExtensibleUser> {
    @Autowired
    @Qualifier("userCSVParser")
    private CSVParser<User> userCSVParser;


    @Override
    protected List<ReconciliationObject<User>> getObjectList(ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList) throws ConnectorDataException {
        try {
            return userCSVParser.getObjects(managedSys, attrMapList, CSVSource.IDM);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new  ConnectorDataException(ErrorCode.CSV_ERROR, e.getMessage());
        }
    }

    @Override
    protected Map<String, ReconciliationResultField> getProvisionMap(ReconciliationObject<User> object, List<AttributeMapEntity> attrMapList) throws ConnectorDataException {
        return userCSVParser.convertToMap(attrMapList, object);
    }
}
