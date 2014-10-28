package org.openiam.elasticsearch.factory;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Properties;

/**
 * Created by: Alexander Duckardt
 * Date: 6/21/14.
 */
@Component
public class ESNodeFactoryBean extends ESAbstractFactoryBean<Node> {

    private static final String DATA_DIR="data";
    private static final String WORK_DIR="work";
    private static final String LOG_DIR="logs";

    private Properties hibernateProperties;

    @Resource(name = "hibernateProperties")
    public void setHibernateProperties(final Properties hibernateProperties) {
        this.hibernateProperties = hibernateProperties;
    }

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

        logger.debug("Starting ElasticSearch node...");
        Node node = nodeBuilder.settings(buildNodeSettings()).node();

        logger.info("Node [" + node.settings().get("name") + "] for [" + node.settings().get("cluster.name") + "] cluster started...");
        logger.debug("  - data : " + node.settings().get("path.data"));
        logger.debug("  - logs : " + node.settings().get("path.logs"));

        return node;
    }

    protected Settings buildNodeSettings() {
        // Build settings
        ImmutableSettings.Builder builder = ImmutableSettings.settingsBuilder()
                                                             .put("node.name", hibernateProperties.getProperty("hibernate.search.default.node.name.prefix") + System.currentTimeMillis())
                                                             .put("cluster.name", hibernateProperties.getProperty("hibernate.search.default.cluster.name"))
                                                             .put("path.data", hibernateProperties.getProperty("hibernate.search.default.indexBase") + "/" + DATA_DIR)
                                                             .put("path.work", hibernateProperties.getProperty("hibernate.search.default.indexBase") + "/" + WORK_DIR)
                                                             .put("path.logs", hibernateProperties.getProperty("hibernate.search.default.indexBase") + "/" + LOG_DIR);

        return builder.build();
    }

    @Override
    public Class<Node> getObjectType() {
        return Node.class;
    }
}
