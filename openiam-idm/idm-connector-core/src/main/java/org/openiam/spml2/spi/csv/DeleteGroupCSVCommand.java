package org.openiam.spml2.spi.csv;

import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.idm.parser.csv.CSVParser;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("deleteGroupCSVCommand")
public class DeleteGroupCSVCommand extends AbstractDeleteCSVCommand<ProvisionGroup>{
    @Autowired
    @Qualifier("groupCsvParser")
    protected CSVParser<Group> groupCsvParser;

    @Override
    protected void deleteObject(String id, ProvisionGroup object, ManagedSysEntity managedSys) throws ConnectorDataException {
        try {
            List<AttributeMapEntity> attrMapList = managedSysService.getResourceAttributeMaps(managedSys.getResourceId());
            groupCsvParser.delete(id, managedSys, attrMapList, CSVSource.IDM);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new  ConnectorDataException(ErrorCode.CSV_ERROR, e.getMessage());
        }
    }
}
