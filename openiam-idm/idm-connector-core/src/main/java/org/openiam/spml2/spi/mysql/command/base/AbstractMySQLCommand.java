package org.openiam.spml2.spi.mysql.command.base;

import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.spml2.msg.RequestType;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.spi.common.AbstractCommand;
import org.openiam.spml2.spi.common.jdbc.JDBCConnectionMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractMySQLCommand<Request extends RequestType, Response extends ResponseType> extends AbstractCommand<Request, Response> {
    @Autowired
    @Qualifier("jdbcConnection")
    protected JDBCConnectionMgr connectionMgr;
}
