package org.openiam.elasticsearch;

import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.stereotype.Component;

/**
 * Created by: Alexander Duckardt
 * Date: 6/21/14.
 */
@Component
public class ESNodeFactoryBean extends ESAbstractFactoryBean<Node> {

    @Override
    public void destroy() throws Exception {
        try {
            logger.info("Closing ElasticSearch node " + this.object.settings().get("name") );
            this.object.close();
        } catch (final Exception e) {
            logger.error("Error closing Elasticsearch node: ", e);
        }
    }

    @Override
    protected Node initialize() throws Exception{
        final NodeBuilder nodeBuilder = NodeBuilder.nodeBuilder();

//        if (null != settingsFile && null == properties) {
//            Settings settings = ImmutableSettings.settingsBuilder()
//                                                 .loadFromClasspath(this.settingsFile)
//                                                 .build();
//            nodeBuilder.getSettings().put(settings);
//        }
//
//        if (null != properties) {
//            nodeBuilder.getSettings().put(properties);
//        }

        logger.debug("Starting ElasticSearch node...");

        Node node = nodeBuilder.node();

        logger.info("Node [" + node.settings().get("name") + "] for [" + node.settings().get("cluster.name") + "] cluster started...");
        logger.debug("  - data : " + node.settings().get("path.data"));
        logger.debug("  - logs : " + node.settings().get("path.logs"));

        return node;
    }
}
