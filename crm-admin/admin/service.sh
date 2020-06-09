#!/bin/bash
#set -x
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" >/dev/null 2>&1 && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
DIR="$( cd -P "$( dirname "$SOURCE" )" >/dev/null 2>&1 && pwd )"

command=$1

source $DIR/../config/application.cfg

app_suffix="-bootable.jar"
base_dir=/var/data/finuser
JAVA_11_HOME=/usr/lib/jvm/jre-11-openjdk/
JAVA_11_BIN=${JAVA_11_HOME}/bin/java
logs_dir=${base_dir}/logs/${APPLICATION_NAME}
work_dir=${base_dir}/work/${APPLICATION_NAME}
app_base_dir=${DIR}/../
pid_file=${work_dir}/${APPLICATION_NAME}.pid

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

if [ -z $APPLICATION_NAME ] ;then
    echo "You must supply a valid application name in configuration file "
    usage
fi

jar_path="${work_dir}/${APPLICATION_NAME}-${APPLICATION_VERSION}${app_suffix}"

#check logs_dir
if [ ! -d ${logs_dir} ];then
    mkdir -p ${logs_dir}
fi

#check work_dir
if [ ! -d ${work_dir} ];then
    mkdir -p ${work_dir}
fi

restart(){
        stop
        status;rtrn=$?
        if [ $rtrn != 0 ];then
                for i in {1..5}
                do
                        sleep 10
                        status;rtrn=$?
                        if [ $rtrn == 0 ];then
                                break
                        fi

                done
        fi
        start
}
start() {
    echo "Starting application ${APPLICATION_NAME}"
    #get status
    status ; rtrn_cd=$?
    if [ $rtrn_cd -eq 1 ];then
        #application running , must stop it first
        echo "application already running"
        exit 0
    fi

    rm -rf ${work_dir}/*
    cp -ar ${app_base_dir}/* ${work_dir}/
    nohup ${JAVA_11_BIN} -jar $jar_path > ${logs_dir}/${APPLICATION_NAME}.log &
    pid=$!
    echo $pid > ${pid_file}
}

stop() {
    echo "Stopping application ${APPLICATION_NAME}"
    status ; rtrn_cd=$?
    if [ $rtrn_cd -eq 1 ];then
        if [ -f ${pid_file} ];then
            pid=`cat ${pid_file}`
        else
            echo "problem reading pid file"
            exit 1
        fi
        echo "killing pid $pid"
        kill -15 $pid
    else
        echo "application not running , then nothing to stop"
    fi
}

#status returns
# 0 : not running
# 1 : running
status(){
    if [ -f ${pid_file} ];then
        pid=`cat ${pid_file}`
    else
        echo "WARNING: pid does not exists for application ${APPLICATION_NAME} assuming it is not running"
        return 0
    fi
    if [ -z $pid ];then
        echo "pid does not exists for application ${APPLICATION_NAME} assuming it is not running"
        return 0
    else
        echo "Getting status of application $APPLICATION_NAME, pid $pid"
        ps -fp $pid > /dev/null ; rtrn_cd=$?
        if [ $rtrn_cd -eq 0 ];then
            #application running
            echo "application ${APPLICATION_NAME} running"
            return 1
        else
            #application not running
            echo "application ${APPLICATION_NAME} NOT running"
            return 0
        fi
    fi

}

eval ${command}
