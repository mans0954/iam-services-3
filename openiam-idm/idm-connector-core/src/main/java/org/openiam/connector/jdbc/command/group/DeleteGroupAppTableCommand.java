package org.openiam.connector.jdbc.command.group;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.jdbc.command.base.AbstractDeleteAppTableCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

@Service("deleteGroupAppTableCommand")
public class DeleteGroupAppTableCommand extends AbstractDeleteAppTableCommand<ExtensibleUser> {
    @Override
    protected AttributeMapEntity getAttribute(List<AttributeMapEntity> attrMap) throws ConnectorDataException {
        for (final AttributeMapEntity atr : attrMap) {
            if (StringUtils.equalsIgnoreCase(atr.getMapForObjectType(), "GROUP_PRINCIPAL")) {
                return atr;
            }
        }
        throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "Attribute not found");
    }

    @Override
    protected String getObjectType() {
        return "GROUP";
    }

}
