#!/bin/bash

set -e
set -x

mvn deploy -s conf/settings.xml
mvn help:evaluate -Dexpression=project.version | grep -v '\[.*'
/bin/sh update_version.sh $(mvn help:evaluate -Dexpression=project.version | grep -v '\[.*')-${CIRCLE_BUILD_NUM}
mvn install -s conf/settings.xml
mvn package spring-boot:repackage -f openiam-esb/pom.xml
mvn deploy -s conf/settings.xml