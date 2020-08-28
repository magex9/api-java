#!/bin/bash
appname=$1

usage(){
	echo "Usage: $0 application_name"
	exit 1

}

if [ "${appname}" == "" ];then
	echo "you need to provide an application name to build"
	usage

fi

#need to run using root if we want to set the ulimit ... to investigate why
buildah bud --ulimit nofile=1048576 -f Dockerfile -t $appname -t w2020.azurecr.io/crm/${appname} --build-arg appname=${appname} .
#buildah bud -f Dockerfile -t crm/$appname -t w2020.azurecr.io/crm/${appname} --build-arg appname=${appname} .
