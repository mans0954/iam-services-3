#!/bin/bash

set -e
set -x

sudo mkdir -p /data/openiam/conf
sudo mkdir -p /etc/chef
sudo mkdir -p /opt/openiam/webapps
sudo cp client.rb /etc/chef/client.rb
sudo cp client.pem /etc/chef/client.pem
sudo cp attributes.json /etc/chef/attributes.json
sudo chef-client -o openiam-properties::datasource -E DEV
sudo chef-client -o openiam-properties::securityconf -E DEV
sudo chef-client -o openiam-properties::service-urls -E DEV
sudo chef-client -o openiam-conf -E DEV
sudo chef-client -o openiam-hazelcast -E DEV -j /etc/chef/attributes.json
sudo apt-add-repository -y 'deb http://ppa.launchpad.net/ondrej/mysql-experimental/ubuntu precise main'
sudo apt-get update; sudo DEBIAN_FRONTEND=noninteractive apt-get install -y mysql-server-5.6
mysql -u root --execute="SET PASSWORD FOR 'root'@'localhost' = PASSWORD('passwd00')"
