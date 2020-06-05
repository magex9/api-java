#!/bin/sh

export MAVEN_REPO=x:/api-build
rm -rf $MAVEN_REPO/local-repo/ca/magex
rm -rf $MAVEN_REPO/deploy-repo/ca/magex
rm -rf $MAVEN_REPO/logs
mkdir -p $MAVEN_REPO/logs

export MAVEN_OPTS="-Dmaven.repo.local=$MAVEN_REPO/local-repo -DaltDeploymentRepository=release-repo::default::file:$MAVEN_REPO/deploy-repo"
echo MAVEN_OPTS=$MAVEN_OTPS

echo Clean
mvn clean | tee -a $MAVEN_REPO/logs/clean.log

echo Install
mvn deploy  | tee -a $MAVEN_REPO/logs/deploy.log

echo Site
mvn site:stage-deploy -DstagingDirectory=$MAVEN_REPO/sites | tee -a $MAVEN_REPO/logs/site.log

echo Display a dependency tree
mvn dependency:tree | tee -a $MAVEN_REPO/logs/dependencies.log

echo Download the findbugs plugin
mvn findbugs:findbugs | tee -a $MAVEN_REPO/logs/findbugs.log

echo Download the cobertura plugin
mvn cobertura:cobertura | tee -a $MAVEN_REPO/logs/cobertura.log

echo Download the jacoco plugin
mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent install -Dmaven.test.failure.ignore=true | tee -a $MAVEN_REPO/logs/jacoco.log
