package org.openiam.idm.srvc.meta.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;
/**
 * Created by: Alexander Duckardt
 * Date: 7/22/14.
 */
@Service("metadataErrorHandler")
public class MetadataErrorHandler implements ErrorHandler {
    private static Logger log = Logger.getLogger(MetadataErrorHandler.class);
    @Override
    public void handleError(Throwable t) {
        log.error(t.getMessage(), t);
    }
}
