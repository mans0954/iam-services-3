package org.openiam.spml2.spi.jdbc.command;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.spi.jdbc.command.base.AbstractDeleteAppTableCommand;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("deleteUserAppTableCommand")
public class DeleteUserAppTableCommand extends AbstractDeleteAppTableCommand<ProvisionUser> {
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
