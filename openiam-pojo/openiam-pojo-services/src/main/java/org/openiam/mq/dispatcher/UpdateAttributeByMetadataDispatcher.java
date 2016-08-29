package org.openiam.mq.dispatcher;

import org.openiam.base.request.UpdateAttributeByMetadataRequest;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMAPICommon;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Created by alexander on 01/08/16.
 */
public abstract class UpdateAttributeByMetadataDispatcher extends AbstractAPIDispatcher<UpdateAttributeByMetadataRequest, Response, OpenIAMAPICommon> {
    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;

    public UpdateAttributeByMetadataDispatcher() {
        super(Response.class);
    }

    @Override
    @Transactional
    protected Response processingApiRequest(final OpenIAMAPICommon openIAMAPI, final UpdateAttributeByMetadataRequest request) throws BasicDataServiceException {
        if(request.isRequired()) {
            final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
            transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
            Boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {
                @Override
                public Boolean doInTransaction(TransactionStatus status) {
                    process(request);
                    return true;
                }
            });
        }
        return new Response(ResponseStatus.SUCCESS);
    }

    protected abstract void process(final UpdateAttributeByMetadataRequest request);
}
