package org.openiam.mq;

import org.openiam.idm.srvc.lang.service.LanguageCountDispatcher;
import org.openiam.idm.srvc.lang.service.LanguageListDispatcher;
import org.openiam.idm.srvc.lang.service.LanguageSaveDispatcher;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 09/08/16.
 */
@Component
public class LanguageListener extends AbstractRabbitMQListener {
    @Autowired
    private LanguageListDispatcher languageListDispatcher;

    @Autowired
    private LanguageSaveDispatcher languageSaveDispatcher;

    @Autowired
    private LanguageCountDispatcher languageCountDispatcher;

    public LanguageListener(OpenIAMQueue queueToListen) {
        super(queueToListen);
    }


    public LanguageListener() {
        this(OpenIAMQueue.LanguageServiceQueue);
    }

    @Override
    protected void doOnMessage(MQRequest message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        OpenIAMAPI apiName = message.getRequestApi();
        switch (apiName){
            case GetUsedLanguages:
            case FindLanguages:
                addTask(languageListDispatcher, correlationId, message, apiName, isAsync);
                break;
            case CountLanguages:
                addTask(languageCountDispatcher, correlationId, message, apiName, isAsync);
                break;
            case SaveLanguage:
                addTask(languageSaveDispatcher, correlationId, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }
}
