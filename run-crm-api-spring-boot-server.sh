#!/bin/bash
podman run -it --rm -e "SPRING_CONFIG_SERVER_URI=http://centralized-configuration-service.466356a5ffaf4931ba8e.canadacentral.aksapp.io/" -e "ENVIRONMENT=ut" crm-api-spring-boot-server /bin/bash 
