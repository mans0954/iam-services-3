package org.openiam.idm.srvc.audit.aspect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.audit.service.AuditLogProvider;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 10/3/13
 * Time: 10:18 PM
 * To change this template use File | Settings | File Templates.
 */
//@Aspect
//@Component
public class AuditLogAspect {
    private static final Log log = LogFactory.getLog(AuditLogAspect.class);

    @Autowired
    protected AuditLogService auditLogService;
    @Autowired
    protected AuditLogProvider auditLogProvider;


    @Pointcut("execution(* org.openiam.idm..*.*(..))")
    public void openiamMethods() {
    }


    @After(value = "openiamMethods() && @annotation(org.openiam.idm.srvc.audit.annotation.AuditLoggable)")
    public void enqueueAuditEvent() {
        log.debug("push audit event to queue");
        auditLogService.enqueue(auditLogProvider.getAuditLogBuilder());
    }

}
