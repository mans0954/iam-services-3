#!/bin/bash

set -e
set -x

wget https://download.elastic.co/elasticsearch/release/org/elasticsearch/distribution/tar/elasticsearch/2.4.3/elasticsearch-2.4.3.tar.gz
tar -xvf elasticsearch-2.4.3.tar.gz
sudo cp elasticsearch.yml elasticsearch-2.4.3/config/elasticsearch.yml
/bin/sh elasticsearch-2.4.3/bin/elasticsearch -d
sleep 10 && wget --waitretry=5 --retry-connrefused -v http://127.0.0.1:9200/

sudo mkdir -p /data/openiam/conf
sudo mkdir -p /data/openiam/logs
sudo mkdir -p /etc/chef
sudo mkdir -p /opt/openiam/webapps
sudo cp esb /etc/init.d/esb
sudo chmod a+x /etc/init.d/esb
sudo cp client.rb /etc/chef/client.rb
sudo cp client.pem /etc/chef/client.pem
sudo cp attributes.json /etc/chef/attributes.json
sudo chmod 777 /opt/openiam/webapps
sudo chef-client -o openiam-devops-app::java8
sudo chef-client -o openiam-devops-properties::datasource -j /etc/chef/attributes.json
sudo chef-client -o openiam-devops-properties::securityconf -j /etc/chef/attributes.json
sudo chef-client -o openiam-devops-properties::service-urls -j /etc/chef/attributes.json
sudo chef-client -o openiam-devops-properties::rabbitmq -j /etc/chef/attributes.json
sudo chef-client -o openiam-devops-conf -j /etc/chef/attributes.json
sudo chef-client -o openiam-devops-hazelcast -j /etc/chef/attributes.json
#sudo chef-client -o openiam-devops-elasticsearch -j /etc/chef/attributes.json
#sudo /etc/init.d/elasticsearch start set -x
#sleep 10 && wget --waitretry=5 --retry-connrefused -v http://127.0.0.1:9200/
sudo chmod 777 /data/openiam/logs
sudo chown ubuntu /data/openiam/logs