package org.openiam.idm.srvc.audit.service;

import java.util.Calendar;
import org.openiam.base.SysConfiguration;
import org.openiam.idm.srvc.audit.dto.SearchAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

/**
 * Implementation class for <code>IdmAuditLogDataService</code>. All audit logging activities 
 * persisted through this service.
 */
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml",
        "classpath:test-application-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class IdmAuditTouchTest extends
        AbstractTransactionalTestNGSpringContextTests {

}
