package org.openiam.elasticsearch.factory;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * Created by alexander on 29.10.14.
 */
public class ESTransportClientFactoryBean extends ESAbstractClientFactoryBean {
    private String[] esNodes;
    private String clusterName;

    public ESTransportClientFactoryBean(String clusterName, String[] esNodes){
        this.esNodes=esNodes;
        this.clusterName=clusterName;
    }

    @Override
    protected TransportClient initialize() throws Exception {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", clusterName).build();
        TransportClient client = new TransportClient(settings);

        for (int i = 0; i < esNodes.length; i++) {
            client.addTransportAddress(toAddress(esNodes[i]));
        }

        return client;
    }

    /**
     * Helper to define an hostname and port with a String like hostname:port
     * @param address Node address hostname:port (or hostname)
     * @return
     */
    private InetSocketTransportAddress toAddress(String address) {
        if (address == null) return null;

        String[] splitted = address.split(":");
        int port = 9300;
        if (splitted.length > 1) {
            port = Integer.parseInt(splitted[1]);
        }
        return new InetSocketTransportAddress(splitted[0], port);
    }

}
