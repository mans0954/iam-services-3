package org.openiam.elasticsearch.factory;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by: Alexander Duckardt
 * Date: 6/21/14.
 */
//@Component("clientFactoryBean")
public class ESClientFactoryBean extends ESAbstractClientFactoryBean {

    private Node node;
    private ESNodeFactoryBean nodeFactory;
//    @Autowired
    public ESClientFactoryBean(ESNodeFactoryBean nodeFactory) throws Exception {
        this.nodeFactory = nodeFactory;
        if(nodeFactory!=null)
            this.node = this.nodeFactory.getObject();
    }

    @Override
    protected Client initialize() throws Exception {
        if (node == null)
            throw new Exception("You must define an ElasticSearch Node as a Spring Bean.");
        return node.client();
    }

    @Override
    public void destroy() throws Exception {
        super.destroy();
        if(nodeFactory!=null)
            nodeFactory.destroy();
    }
}
