package org.openiam.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by: Alexander Duckardt
 * Date: 6/21/14.
 */
@Component
public class ESClientFactoryBean extends ESAbstractFactoryBean<Client> {

    private Node node;

    @Autowired
    public ESClientFactoryBean(ESNodeFactoryBean nodeFactory) throws Exception {
        this.node = nodeFactory.getObject();
    }

    @Override
    protected Client initialize() throws Exception {
        if (node == null)
            throw new Exception("You must define an ElasticSearch Node as a Spring Bean.");
        return node.client();
    }

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
