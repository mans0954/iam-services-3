package org.openiam.idm.srvc.msg.service;

import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;

@Service("mailErrorHandler")
public class MailErrorHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable t) {
        //TODO: add error handling
    }
}
