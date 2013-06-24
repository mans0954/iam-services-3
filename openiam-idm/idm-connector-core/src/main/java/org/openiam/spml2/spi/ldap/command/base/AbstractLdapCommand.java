package org.openiam.spml2.spi.ldap.command.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemObjectMatchDAO;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.common.AbstractCommand;
import org.openiam.spml2.spi.common.ConnectorCommand;
import org.openiam.spml2.util.connect.ConnectionFactory;
import org.openiam.spml2.util.connect.ConnectionManagerConstant;
import org.openiam.spml2.util.connect.ConnectionMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

public abstract class AbstractLdapCommand<Request extends RequestType, Response extends ResponseType>  extends AbstractCommand<Request, Response> {
    protected final Log log = LogFactory.getLog(this.getClass());


    protected LdapContext connect(ManagedSysEntity managedSys) throws ConnectorDataException {
        ConnectionMgr conMgr = ConnectionFactory.create(ConnectionManagerConstant.LDAP_CONNECTION);
        conMgr.setApplicationContext(this.applicationContext);
        log.debug("Connecting to directory:  " + managedSys.getName());

        LdapContext ldapctx = null;
        try {
            ldapctx = conMgr.connect(managedSys);
            log.debug("Ldapcontext = " + ldapctx);

            if (ldapctx == null) {
                throw  new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, "Unable to connect to directory.");
            }
        } catch (NamingException e) {
            log.error(e.getMessage(), e);
            throw  new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, e.getMessage());
        }
        return ldapctx;
    }

}
