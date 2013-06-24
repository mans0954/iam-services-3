package org.openiam.spml2.spi.csv.command;

import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.idm.parser.csv.CSVParser;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.provision.dto.ProvisionGroup;
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

@Service("lookupGroupCSVCommand")
public class LookupGroupCSVCommand extends AbstractLookupCSVCommand<Group, ProvisionGroup> {
    @Autowired
    @Qualifier("groupCsvParser")
    protected CSVParser<Group> groupCsvParser;

    @Override
    protected List<ReconciliationObject<Group>> getObjectList(ManagedSysEntity managedSys) throws ConnectorDataException {
        try {
            List<AttributeMapEntity> attrMapList = managedSysService
                .getResourceAttributeMaps(managedSys.getResourceId());
            return groupCsvParser.getObjects(managedSys, attrMapList, CSVSource.IDM);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new  ConnectorDataException(ErrorCode.CSV_ERROR, e.getMessage());
        }
    }

    @Override
    protected boolean match(String findValue, ReconciliationObject<Group> object, ExtensibleObject extObject) throws ConnectorDataException {
        if (!StringUtils.hasText(findValue) || object == null) {
            return false;
        }
        if (findValue.equals(object.getPrincipal())) {
            extObject.setObjectId(object.getPrincipal());
            return true;
        }
        return false;
    }

    @Override
    protected Map<String, String> getProvisionMap(ReconciliationObject<Group> object, List<AttributeMapEntity> attrMapList) throws ConnectorDataException {
        return groupCsvParser.convertToMap(attrMapList, object);
    }
}
