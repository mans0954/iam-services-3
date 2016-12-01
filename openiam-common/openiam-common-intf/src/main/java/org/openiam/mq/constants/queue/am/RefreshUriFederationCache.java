package org.openiam.mq.constants.queue.am;

import org.openiam.mq.constants.RabbitMqExchange;

/**
 * Created by alexander on 15/11/16.
 */
public class RefreshUriFederationCache extends AMQueue {
    public RefreshUriFederationCache() {
        super(RabbitMqExchange.URI_FEDERATION_CACHE_EXCHANGE, RefreshOAuthCache.class.getSimpleName());
    }
}
