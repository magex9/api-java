#!/bin/bash
appname=$1
keypass=$2
alias=${3:appname}
namespace=appdev

check_rtrn_cd(){
if [ $1 -ne 0 ];then
	echo "command failed"
	exit 1
fi
}

if [ -f "${appname}.jks" ];then
	rm ${appname}.jks
fi

if [ -f "${appname}.cer" ];then
	rm ${appname}.cer
fi


keytool -genkey -storepass "${keypass}" -storetype "jks" -alias ${alias} -keyalg RSA -keystore ${appname}.jks -keysize 4096
check_rtrn_cd $?

keytool -export -storepass "${keypass}" -storetype "jks" -keystore ${appname}.jks -alias ${alias} -file ${appname}.cer
check_rtrn_cd $?

kubectl delete secret -n ${namespace} ${appname}-cert
kubectl create secret generic -n appdev ${appname}-cert --from-file=./${appname}.cer --from-file=${appname}.jks
check_rtrn_cd $?

kubectl delete secret -n ${namespace} ${appname}-keypass
kubectl create secret generic -n appdev ${appname}-keypass --from-literal=jwt.rsa.keypass="${keypass}"
check_rtrn_cd $?
