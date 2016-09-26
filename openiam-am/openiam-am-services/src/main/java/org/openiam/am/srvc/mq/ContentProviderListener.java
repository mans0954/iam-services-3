package org.openiam.am.srvc.mq;

import org.openiam.am.srvc.service.dispatcher.*;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.mq.constants.ContentProviderAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 23/09/16.
 */
@Component
public class ContentProviderListener extends AbstractRabbitMQListener<ContentProviderAPI> {
    @Autowired
    private GetAllMetaTypeDispatcher getAllMetaTypeDispatcher;
    @Autowired
    private GetAuthLevelAttributeDispatcher getAuthLevelAttributeDispatcher;
    @Autowired
    private GetAuthLevelGroupingDispatcher getAuthLevelGroupingDispatcher;
    @Autowired
    private GetAuthLevelGroupingListDispatcher getAuthLevelGroupingListDispatcher;
    @Autowired
    private GetAuthLevelListDispatcher getAuthLevelListDispatcher;
    @Autowired
    private GetContentProviderDispatcher getContentProviderDispatcher;
    @Autowired
    private GetNumOfContentProviderDispatcher getNumOfContentProviderDispatcher;
    @Autowired
    private GetNumOfUriPatternDispatcher getNumOfUriPatternDispatcher;
    @Autowired
    private GetUriPatternProviderDispatcher getUriPatternProviderDispatcher;
    @Autowired
    private FindContentProvidersDispatcher findContentProvidersDispatcher;
    @Autowired
    private FindUriPatternDispatcher findUriPatternDispatcher;
    @Autowired
    private CreateDefaultURIPatternsDispatcher createDefaultURIPatternsDispatcher;
    @Autowired
    private SaveAuthLevelAttributeDispatcher saveAuthLevelAttributeDispatcher;
    @Autowired
    private SaveAuthLevelGroupingDispatcher saveAuthLevelGroupingDispatcher;
    @Autowired
    private SaveContentProviderDispatcher saveContentProviderDispatcher;
    @Autowired
    private SaveUriPatternDispatcher saveUriPatternDispatcher;
    @Autowired
    private DeleteContentProviderDispatcher deleteContentProviderDispatcher;

    public ContentProviderListener() {
        super(OpenIAMQueue.ContentProviderQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, ContentProviderAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        ContentProviderAPI apiName = message.getRequestApi();
        switch (apiName){
            case GetAllMetaType:
                addTask(getAllMetaTypeDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetAuthLevelAttribute:
                addTask(getAuthLevelAttributeDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetAuthLevelGrouping:
                addTask(getAuthLevelGroupingDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetAuthLevelGroupingList:
                addTask(getAuthLevelGroupingListDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetAuthLevelList:
                addTask(getAuthLevelListDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetContentProvider:
                addTask(getContentProviderDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetNumOfContentProviders:
                addTask(getNumOfContentProviderDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetNumOfUriPatterns:
                addTask(getNumOfUriPatternDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetURIPattern:
                addTask(getUriPatternProviderDispatcher, correlationId, message, apiName, isAsync);
                break;
            case FindContentProviderBeans:
                addTask(findContentProvidersDispatcher, correlationId, message, apiName, isAsync);
                break;
            case FindUriPatterns:
                addTask(findUriPatternDispatcher, correlationId, message, apiName, isAsync);
                break;
            case CreateDefaultURIPatterns:
                addTask(createDefaultURIPatternsDispatcher, correlationId, message, apiName, isAsync);
                break;
            case SaveAuthLevelAttribute:
                addTask(saveAuthLevelAttributeDispatcher, correlationId, message, apiName, isAsync);
                break;
            case SaveAuthLevelGrouping:
                addTask(saveAuthLevelGroupingDispatcher, correlationId, message, apiName, isAsync);
                break;
            case SaveContentProvider:
            case SetupApplication:
                addTask(saveContentProviderDispatcher, correlationId, message, apiName, isAsync);
                break;
            case SaveURIPattern:
                addTask(saveUriPatternDispatcher, correlationId, message, apiName, isAsync);
                break;
            case DeleteAuthLevelAttribute:
            case DeleteAuthLevelGrouping:
            case DeleteContentProvider:
            case DeleteProviderPattern:
                addTask(deleteContentProviderDispatcher, correlationId, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }
}
