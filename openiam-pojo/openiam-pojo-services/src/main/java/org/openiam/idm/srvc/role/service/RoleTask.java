package org.openiam.idm.srvc.role.service;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Component("roleTask")
public class RoleTask implements Sweepable {
    private static final Log log = LogFactory.getLog(RoleTask.class);

    private final Object mutex = new Object();

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private RoleDataService roleDataService;

    //TODO change when Spring 3.2.2 @Scheduled(fixedDelayString = "${org.openiam.org.manager.threadsweep}")
    @Scheduled(cron="0 30 3 * * ?")
    public void sweep() {
        final StopWatch sw = new StopWatch();
        sw.start();
        final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
        String res = transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                synchronized (mutex) {
                    roleDataService.rebuildRoleHierarchyCache();
                }
                return "OK";
            }
        });

        sw.stop();
        if(log.isDebugEnabled()) {
        	log.debug(String.format("Done roles HierarchyCache rebuild. Took: %s ms", sw.getTime()));
        }
    }

}
