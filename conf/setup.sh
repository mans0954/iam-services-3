#!/bin/bash

set -e
set -x 

mkdir -p /data/openiam/conf
mkdir -p /etc/chef
mkdir -p /opt/openiam/webapps
cp conf/client.rb /etc/chef/client.rb
cp conf/client.pem /etc/chef/client.pem
cp conf/attributes.json /etc/chef/attributes.json
chef-client -o openiam-properties::datasource -E DEV
chef-client -o openiam-properties::securityconf -E DEV
chef-client -o openiam-properties::service-urls -E DEV
chef-client -o openiam-conf -E DEV
chef-client -o openiam-hazelcast -E DEV -j /etc/chef/attributes.json
mysql -u root --execute="SET PASSWORD FOR 'root'@'localhost' = PASSWORD('passwd00')"