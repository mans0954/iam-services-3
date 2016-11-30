package org.openiam.mq.constants.queue.am;

import org.openiam.mq.constants.RabbitMqExchange;

/**
 * Created by alexander on 15/11/16.
 */
public class RefreshOAuthCache extends AMQueue {
    public RefreshOAuthCache() {
        super(RabbitMqExchange.REFRESH_OAUTH_CACHE_EXCHANGE, RefreshOAuthCache.class.getSimpleName());
    }
}
