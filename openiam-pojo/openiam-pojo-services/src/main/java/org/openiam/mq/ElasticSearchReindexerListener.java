package org.openiam.mq;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.EmptyServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.request.IdmAuditLogRequest;
import org.openiam.base.request.model.AuditLogBatchContainer;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.concurrent.AuditLogHolder;
import org.openiam.elasticsearch.model.ElasticsearchReindexRequest;
import org.openiam.elasticsearch.service.ElasticsearchReindexProcessor;
import org.openiam.elasticsearch.service.ElasticsearchReindexService;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.AuditLogTargetEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.api.AuditLogAPI;
import org.openiam.mq.constants.api.EsAPI;
import org.openiam.mq.constants.queue.audit.AuditLogQueue;
import org.openiam.mq.constants.queue.common.EsReindexQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * Created by alexander on 01/08/16.
 */
@Component
@RabbitListener(id="elasticSearchReindexerListener",
        queues = "#{EsReindexQueue.name}",
        containerFactory = "commonRabbitListenerContainerFactory")
public class ElasticSearchReindexerListener extends AbstractListener<EsAPI> {
    @Autowired
    public ElasticSearchReindexerListener(EsReindexQueue queue) {
        super(queue);
    }
    @Autowired
    private ElasticsearchReindexService elasticsearchReindexService;
    @Autowired
    private ElasticsearchReindexProcessor reindexer;

    protected RequestProcessor<EsAPI, EmptyServiceRequest> getEmptyRequestProcessor(){
        return new RequestProcessor<EsAPI, EmptyServiceRequest>(){
            @Override
            public Response doProcess(EsAPI esAPI, EmptyServiceRequest request) throws BasicDataServiceException {
                ClassListResponse response = new ClassListResponse();
                response.setList(reindexer.getIndexedClasses());
                return response;
            }
        };
    }


    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) EsAPI api, ElasticsearchReindexRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<EsAPI, ElasticsearchReindexRequest>(){
            @Override
            public Response doProcess(EsAPI api, ElasticsearchReindexRequest request) throws BasicDataServiceException {
                if(CollectionUtils.isEmpty(request.getEntityIdList())){
                    final IdmAuditLogEntity auditLog = AuditLogHolder.getInstance().getEvent();
                    auditLog.setAction(AuditAction.REINDEX.value());
                    auditLog.setTargetClass(request.getEntityClass());

                    int numOfReindexedEntities = reindexer.reindex(request.getEntityClass());
                    IntResponse response = new IntResponse();
                    response.setValue(numOfReindexedEntities);
                    return response;
                } else {
                    reindexer.reindex(request.getEntityClass(), request.getEntityIdList());
                    return new Response();
                }
            }
        });
    }
}
