package org.openiam.idm.srvc.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.report.dto.ReportSubscriptionDto;
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.Enumeration;

@Component("subscriptionDispatcher")
public class SubscriptionDispatcher implements Sweepable {

    private static final Log log = LogFactory
            .getLog(SubscriptionDispatcher.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier(value = "subsQueue")
    private Queue queue;

    @Autowired
    private ReportingTask reportingTask;

    @Override
    public void sweep() {
        jmsTemplate.browse(queue, new BrowserCallback<Object>() {
            @Override
            public Object doInJms(Session session, QueueBrowser browser) throws JMSException {
                int numMsg = 0;
                Enumeration e = browser.getEnumeration();
                while (e.hasMoreElements()) {
                    e.nextElement();
                    numMsg++;
                }
                for (int i = 0; i < numMsg; i++) {
                    final ReportSubscriptionDto report = ((ReportSubscriptionDto) ((ObjectMessage) jmsTemplate.receive(queue)).getObject());
                    log.debug("Report browsed " + report.getReportName());
                    reportingTask.asyncExecuteReport(report);
                }
                return null;
            }
        });
    }
}
