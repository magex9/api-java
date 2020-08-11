#!/bin/bash
buildah tag crm-api-spring-boot-server:latest w2020.azurecr.io/crm-api-spring-boot-server:latest
buildah push w2020.azurecr.io/crm-api-spring-boot-server:latest
