package org.openiam.idm.srvc.org.service;

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

@Component("organizationTypeTask")
public class OrganizationTypeTask implements Sweepable {

    private static final Log log = LogFactory.getLog(OrganizationTypeTask.class);

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private OrganizationTypeService organizationTypeService;

    private final Object mutex = new Object();

    @Override
    //TODO change when Spring 3.2.2 @Scheduled(fixedDelayString = "${org.openiam.org.manager.threadsweep}")
    @Scheduled(fixedDelay=300000)
    public void sweep() {
        final StopWatch sw = new StopWatch();
        sw.start();
        final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
        String res = transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                synchronized(mutex) {
                    organizationTypeService.fireUpdateOrgTypeMap();
                }
                return "OK";
            }
        });

        sw.stop();
        log.debug(String.format("Done creating orgs trees. Took: %s ms", sw.getTime()));
    }


}
