#!/bin/bash

set -e
set -x

sudo mkdir -p /data/openiam/conf
sudo mkdir -p /etc/chef
sudo mkdir -p /opt/openiam/webapps
sudo cp client.rb /etc/chef/client.rb
sudo cp client.pem /etc/chef/client.pem
sudo cp attributes.json /etc/chef/attributes.json
sudo chmod 777 /opt/openiam/webapps
sudo chef-client -o openiamapp::java8
sudo chef-client -o openiam-properties::datasource -j /etc/chef/attributes.json
sudo chef-client -o openiam-properties::securityconf -j /etc/chef/attributes.json
sudo chef-client -o openiam-properties::service-urls -j /etc/chef/attributes.json
sudo chef-client -o openiam-conf -j /etc/chef/attributes.json
sudo chef-client -o openiam-hazelcast -j /etc/chef/attributes.json