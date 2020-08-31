#/bin/bash
secret_name=$1
from_namespace=$2
to_namespace=$3

usage(){
	echo "$0 secret_name from_namespace to_namespace"
	exit 1
}

kubectl get secret $secret_name -n $from_namespace -o yaml \
| sed s/"namespace: ${from_namespace}"/"namespace: ${to_namespace}"/\
| kubectl apply -n ${to_namespace} -f -

