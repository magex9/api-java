#!/bin/bash
command=$1
app_name=$2
app_suffix="-bootable.jar"


usage(){
    echo ""
    echo "Usage: $0 start|stop|restart|status application_name"
    echo "Description: This service.sh command is to manage springboot applications bootable jars"
    echo ""
    exit 1
}


case $command in
    "start" | "stop" | "restart" | "status" ) echo $command ;;
    *) echo "command must be either start,stop,restart or status" ; usage ;;
esac

#check application name
ls $app_name > /dev/null
app_exists=$?
echo $app_exists
if [ -z $app_name ] || [ ${app_exists} == 2 ];then
    echo "You must supply a valid application name at argument 2"
    usage
fi

jar_path="$app_name/target/$app_name-*.*.*${app_suffix}"

ls -la $jar_path


start() {
    nohup java -jar $jar_path > logs/${app_name}.log &
    pid=$!
    echo $pid > ${app_name}.pid
}

stop() {
    pid=cat ${app_name}.pid
    if [ -z $pid ];then
        echo "pid does not exists for application ${app_name} assuming it is not running"
    else
        echo "killing pid $pid"
        kill -15 $pid
    fi
}

status(){
    pid=`cat ${app_name}.pid`
    if [ -z $pid ];then
        echo "pid does not exists for application ${app_name} assuming it is not running"
    else
        echo "Getting status of application $app_name, pid $pid"
        ps -fp $pid
    fi

}

eval ${command}