#!/bin/bash
buildah tag crm-api-spring-boot-server:latest w2020.azurecr.io/crm-api-spring-boot-server:latest
buildah push w2020.azurecr.io/crm-api-spring-boot-server:latest
helm upgrade --force --install --namespace appdev --recreate-pods crm-api-spring-boot-server helm-charts/crm-api-spring-boot-server
