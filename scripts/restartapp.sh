#!/bin/bash
set -x
namespace=$1
appname=$2
kubectl -n "$namespace" scale --replicas=0 deployment/${appname}
kubectl -n "$namespace" scale --replicas=1 deployment/${appname}
 
