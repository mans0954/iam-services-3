package org.openiam.connector.csv.command.group;

import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.idm.parser.csv.GroupCsvParser;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.type.ExtensibleGroup;
import org.openiam.connector.csv.command.base.AbstractTestCSVCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service("testGroupCSVCommand")
public class TestGroupCSVCommand extends AbstractTestCSVCommand<Group, ExtensibleGroup> {
    @Autowired
    @Qualifier("groupCsvParser")
    protected GroupCsvParser groupCsvParser;

    @Override
    protected List<ReconciliationObject<Group>> getObjectList(ManagedSysEntity managedSys) throws ConnectorDataException {
        try {
        	List<AttributeMapEntity> attrMapList = (managedSys.getResource() != null) ? managedSysService.getResourceAttributeMaps(managedSys.getResource().getId()) : Collections.EMPTY_LIST;
            return groupCsvParser.getObjects(managedSys, attrMapList, CSVSource.IDM);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new  ConnectorDataException(ErrorCode.CSV_ERROR, e.getMessage());
        }
    }
}
