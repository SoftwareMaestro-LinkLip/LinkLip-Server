#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/app

lsof -P -i :8080 |grep java |awk '{print $2}' |xargs kill -9

JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

chmod +x $JAR_NAME

nohup java -jar -Dspring.profiles.active=dev $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &