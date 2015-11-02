#!/bin/bash

set -e
set -x

mvn deploy -s settings.xml
/bin/sh ../update_version.sh $(mvn help:evaluate -Dexpression=project.version | grep -v '\[.*')-${CIRCLE_BUILD_NUM}
install -s settings.xml
mvn package spring-boot:repackage -f ../openiam-esb/pom.xml
mvn deploy settings.xml