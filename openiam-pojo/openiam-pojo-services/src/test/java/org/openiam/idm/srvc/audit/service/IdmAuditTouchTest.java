package org.openiam.idm.srvc.audit.service;

import java.util.Calendar;
import org.openiam.base.SysConfiguration;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
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
    @Autowired
    private IdmAuditLogDataService auditDataService;

    @Test
    public void addLog() {
        auditDataService.addLog(new IdmAuditLog());
    }

    @Test
    public void updateLog() {
        IdmAuditLog idm = new IdmAuditLog();
        idm = auditDataService.addLog(idm);
        idm.setHost("asd");
        auditDataService.updateLog(idm);
    }

    @Test
    public void getCompleteLog() {
        auditDataService.getCompleteLog();
    }

    @Test
    public void getPasswordChangeLog() {
        auditDataService.getPasswordChangeLog();
    }

    @Test
    public void search() {
        auditDataService.search(new SearchAudit());
    }

    @Test
    public void eventsAboutUser() {
        SysConfiguration sc = new SysConfiguration();
        sc.setDefaultManagedSysId("");
        sc.setDefaultSecurityDomain("");
        auditDataService.setSysConfiguration(sc);
        auditDataService.eventsAboutUser("", Calendar.getInstance().getTime());
    }
}
