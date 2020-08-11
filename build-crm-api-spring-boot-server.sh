#!/bin/bash

#buildah bud --ulimit nofile=1048576 -f crm-api-spring-boot-server.dockerfile -t crm-api-spring-boot-server .
buildah bud -f crm-api-spring-boot-server.dockerfile -t crm-api-spring-boot-server -t .

