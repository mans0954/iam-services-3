package org.openiam.spml2.spi.orcl.command.base;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.spml2.spi.common.jdbc.AbstractJDBCCommand;
import org.openiam.spml2.util.msg.ResponseBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/11/13
 * Time: 10:23 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractOracleCommand<Request extends RequestType, Response extends ResponseType> extends AbstractJDBCCommand<Request, Response> {


    protected String getResourceId(String targetID, ManagedSysEntity managedSys) throws ConnectorDataException {
        if(managedSys == null)
            throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, String.format("No Managed System with target id: %s", targetID));

        if (StringUtils.isBlank(managedSys.getResourceId()))
            throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "ResourceID is not defined in the ManagedSys Object");

        final Resource res = resourceDataService.getResource(managedSys.getResourceId());
        if(res == null)
            throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "No resource for managed resource found");

        return managedSys.getResourceId();
    }
}
