#!/bin/sh
#
#
#
#
start() {
cd /home/pi/Interceptor
java -cp ./forms_rt-7.0.3.jar:./forms-1.1-preview.jar:./jSerialComm-2.6.2.jar:./pi4j-core.jar:Interceptor.jar de.dbconsult.interceptor.Interceptor mill USB pc AMA0 &
}

case "$1" in
  start)
	start
	;;
esac
exit 0
