package org.openiam.connector.orcl.command.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.orcl.command.base.AbstractModifyOracleCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/11/13
 * Time: 10:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("modifyUserOracleCommand")
public class ModifyUserOracleCommand extends AbstractModifyOracleCommand<ExtensibleUser> {

    @Override
    protected void modifyObject(CrudRequest<ExtensibleUser> crudRequest, List<AttributeMapEntity> attributeMap, Connection con) throws ConnectorDataException {

    	//username cannot be altered in Oracle yet

        ExtensibleObject obj = crudRequest.getExtensibleObject();

        String origIdentity = null;
        if (StringUtils.isNotBlank(crudRequest.getObjectIdentity())) {
        	if(log.isDebugEnabled()) {
        		log.debug("Modify user:" + crudRequest.getObjectIdentity());
        	}
            for (ExtensibleAttribute att : obj.getAttributes()) {
                if ("ORIG_IDENTITY".equals(att.getName())) {
                    origIdentity = att.getValue();
                }
            }
        }

        if (StringUtils.isNotBlank(origIdentity)
                && !StringUtils.equals(origIdentity, crudRequest.getObjectIdentity())) {

            throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION,
                    "Identity rename is not supported");
        }
    }
}
