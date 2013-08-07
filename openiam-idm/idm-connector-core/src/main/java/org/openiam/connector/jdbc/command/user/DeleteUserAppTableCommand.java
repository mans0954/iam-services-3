package org.openiam.connector.jdbc.command.user;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.connector.jdbc.command.base.AbstractDeleteAppTableCommand;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("deleteUserAppTableCommand")
public class DeleteUserAppTableCommand extends AbstractDeleteAppTableCommand<ExtensibleUser> {
    @Override
    protected AttributeMapEntity getAttribute(List<AttributeMapEntity> attrMap) throws ConnectorDataException {
        AttributeMapEntity result = null;
        for (final AttributeMapEntity atr : attrMap) {
            if (StringUtils.equalsIgnoreCase(atr.getMapForObjectType(), "principal")) {
                return atr;
            }
        }
        throw  new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "Attribute not found");
    }
}
