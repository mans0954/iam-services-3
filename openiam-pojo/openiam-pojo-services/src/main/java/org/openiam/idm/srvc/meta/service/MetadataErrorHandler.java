package org.openiam.idm.srvc.meta.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;
/**
 * Created by: Alexander Duckardt
 * Date: 7/22/14.
 */
@Service("metadataErrorHandler")
public class MetadataErrorHandler implements ErrorHandler {
	private static final Log log = LogFactory.getLog(MetadataErrorHandler.class);
    @Override
    public void handleError(Throwable t) {
        log.error(t.getMessage(), t);
    }
}
