#!/bin/bash

set -e
set -x

current_version=$(mvn help:evaluate -Dexpression=project.version | grep -v '\[.*')-${CIRCLE_BUILD_NUM}
base_artifactory_url="https://openiam.artifactoryonline.com/openiam/libs-releases-local"

artifact_list=(
	am-core
	iam-services
	idm-connector-core
	idm-connector-intf
	idm-core
	km-core
	openiam-am-intf
	openiam-am-services
	openiam-auth-manager
	openiam-auth-manager-client
	openiam-auth-manager-intf
	openiam-auth-manager-service
	openiam-auth-manager-web
	openiam-bpm
	openiam-bpm-activiti
	openiam-common
	openiam-common-db-intf
	openiam-common-intf
	openiam-idm-intf
	openiam-idm-services
	openiam-jaas
	openiam-km-common
	openiam-km-migrate-tool
	openiam-km-util
	openiam-pojo
	openiam-pojo-intf
	openiam-pojo-services
	openiam-security
	openiam-sso-utils
)

mvn deploy -s conf/settings.xml
mvn help:evaluate -Dexpression=project.version | grep -v '\[.*'
/bin/sh update_version.sh ${current_version}
mvn install -s conf/settings.xml
mvn package spring-boot:repackage -f openiam-esb/pom.xml
mvn deploy -s conf/settings.xml


for artifact in "${artifact_list[@]}"
do
  	curl -u sysadmin:${OPENIAM_ARTIFACTORY_PASSWORD} -X DELETE "${base_artifactory_url}/org/openiam/${artifact}/${current_version}/${artifact}-${current_version}.jar"
done