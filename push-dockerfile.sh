#!/bin/bash
appname=$1
version=$2
buildah tag "localhost/crm/${appname}:latest" "w2020.azurecr.io/crm/${appname}:${version}"
buildah push "w2020.azurecr.io/crm/${appname}:${version}"
helm upgrade --install --set application.version=${version} --namespace appdev --recreate-pods ${appname} helm-charts/${appname}
