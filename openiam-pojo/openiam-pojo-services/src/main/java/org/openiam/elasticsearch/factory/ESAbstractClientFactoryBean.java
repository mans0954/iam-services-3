package org.openiam.elasticsearch.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.client.Client;

/**
 * Created by alexander on 29.10.14.
 */
public abstract class ESAbstractClientFactoryBean extends ESAbstractFactoryBean<Client> {
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public void destroy() throws Exception {
        try {
            logger.info("Closing ElasticSearch client");
            if (object != null) {
                object.close();
            }
        } catch (final Exception e) {
            logger.error("Error closing ElasticSearch client: ", e);
        }
    }

    @Override
    public Class<Client> getObjectType() {
        return Client.class;
    }
}
