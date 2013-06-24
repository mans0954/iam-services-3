package org.openiam.spml2.spi.csv.command;

import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.idm.parser.csv.CSVParser;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.spi.csv.command.base.AbstractLookupCSVCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service("lookupUserCSVCommand")
public class LookupUserCSVCommand extends AbstractLookupCSVCommand<User, ProvisionUser> {
    @Autowired
    @Qualifier("userCSVParser")
    private CSVParser<User> userCSVParser;


    @Override
    protected List<ReconciliationObject<User>> getObjectList(ManagedSysEntity managedSys) throws ConnectorDataException {
        try {
            List<AttributeMapEntity> attrMapList = managedSysService
                .getResourceAttributeMaps(managedSys.getResourceId());
            return userCSVParser.getObjects(managedSys, attrMapList, CSVSource.IDM);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new  ConnectorDataException(ErrorCode.CSV_ERROR, e.getMessage());
        }
    }

    protected boolean match(String findValue, ReconciliationObject<User> user2, ExtensibleObject extOnject) throws ConnectorDataException {
        if (!StringUtils.hasText(findValue) || user2 == null) {
            return false;
        }
        if (findValue.equals(user2.getPrincipal())) {
            extOnject.setObjectId(user2.getPrincipal());
            return true;
        }
        return false;
    }

    @Override
    protected Map<String, String> getProvisionMap(ReconciliationObject<User> object, List<AttributeMapEntity> attrMapList) throws ConnectorDataException {
        return userCSVParser.convertToMap(attrMapList, object);
    }
}
