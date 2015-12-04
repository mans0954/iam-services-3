package org.openiam.config;

import java.net.InetSocketAddress;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.NodeBuilder;
import org.openiam.elasticsearch.mapper.AnnotationEntityMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories("org.openiam.elasticsearch.dao")
public class ElasticSearchConfig {
	
	private static final String DATA_DIR="data";
    private static final String WORK_DIR="work";
    private static final String LOG_DIR="logs";

    @Value("${org.openiam.es.client}")
    private String clientType;
    
    @Value("${org.openiam.es.external.nodes}")
    private String esNodes;
    
    @Value("${org.openiam.es.default.cluster.name}")
    private String clusterName;
    
    @Value("${hibernate.search.default.indexBase}")
    private String hibernateSearchBase;
    
	@Bean(name="client")
	public Client client() {
		
		Client client = null;
		final NodeBuilder builder = NodeBuilder.nodeBuilder().clusterName(clusterName);
		builder.settings(ImmutableSettings.builder().put("cluster.name", clusterName)
													.put("path.data", hibernateSearchBase + "/" + DATA_DIR)
													.put("path.work", hibernateSearchBase + "/" + WORK_DIR)
													.put("path.logs", hibernateSearchBase + "/" + LOG_DIR).build());
		if(StringUtils.equalsIgnoreCase(clientType, "embedded")) {
			client = builder.local(true).node().start().client();
		} else { /* external */
			client = builder.local(false).client(true).build().client();
			if(!(client instanceof TransportClient)) {
				throw new RuntimeException(String.format("Expected external elastic search to be of type: %s, but was: %s", TransportClient.class, client.getClass().getCanonicalName()));
			}
			for(String ip : StringUtils.split(esNodes, ",")) {
				ip = StringUtils.trimToNull(ip);
				if(ip != null) {
					int port = 9300;
					String hostname = null;
					if(ip.contains(":")) {
						port = Integer.valueOf(ip.split(":")[1]);
						hostname = ip.split(":")[0];
					} else {
						hostname = ip;
					}
					((TransportClient)client).addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(hostname, port)));
				}
			}
		}
		return client;
	}
	
	
	@Bean(name="elasticsearchTemplate")
	public ElasticsearchTemplate elasticsearchTemplate() {
		final ElasticsearchTemplate template = new ElasticsearchTemplate(client(), new AnnotationEntityMapper());
		return template;
	}
}
