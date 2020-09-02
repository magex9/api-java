#!/bin/bash
registry="w2020.azurecr.io"
token=`az acr login --name $registry --expose-token`
echo $token > token.txt
accessToken=`echo $token | jq -r '.accessToken'` 
username="00000000-0000-0000-0000-000000000000"
buildah login --username=${username} --password="${accessToken}" $registry
