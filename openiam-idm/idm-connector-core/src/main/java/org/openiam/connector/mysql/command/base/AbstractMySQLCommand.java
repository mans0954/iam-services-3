package org.openiam.connector.mysql.command.base;

import org.openiam.provision.request.RequestType;
import org.openiam.base.response.ResponseType;
import org.openiam.connector.common.command.AbstractCommand;
import org.openiam.connector.common.jdbc.JDBCConnectionMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractMySQLCommand<Request extends RequestType, Response extends ResponseType> extends AbstractCommand<Request, Response> {
    @Autowired
    @Qualifier("jdbcConnection")
    protected JDBCConnectionMgr connectionMgr;
}
