#!/bin/sh

mvn install:install-file -DgroupId=com.google.gdata -DartifactId=gdata-appsforyourdomain-1.0 -Dversion=1.41.5 -Dpackaging=jar -Dfile=gdata-appsforyourdomain-1.0-1.41.5.jar

mvn install:install-file -DgroupId=com.google.gdata -DartifactId=gdata-core-1.0 -Dversion=1.41.5 -Dpackaging=jar -Dfile=gdata-core-1.0-1.41.5.jar

mvn install:install-file -DgroupId=com.google.gdata -DartifactId=gdata-client-1.0 -Dversion=1.41.5 -Dpackaging=jar -Dfile=gdata-client-1.0-1.41.5.jar

mvn install:install-file -DgroupId=com.google.gdata -DartifactId=gdata-analytics-2.1 -Dversion=1.41.5 -Dpackaging=jar -Dfile=gdata-analytics-2.1-1.41.5.jar

mvn install:install-file -DgroupId=ch.ethz.ssh2 -DartifactId=ganymed-ssh2 -Dversion=251beta1 -Dpackaging=jar -Dfile=ganymed-ssh2-build251beta1.jar

mvn install:install-file -DgroupId=com.sforce.soap.partner -DartifactId=salesforce.partner -Dversion=12.0 -Dpackaging=jar -Dfile=salesforce.partner.jar

mvn install:install-file -DgroupId=com.mchange -DartifactId=c3p0 -Dversion=0.9.5.2 -Dpackaging=jar -Dfile=c3p0-0.9.5.2.jar
