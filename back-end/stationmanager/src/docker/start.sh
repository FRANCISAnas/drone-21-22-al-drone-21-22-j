#!/bin/bash


wait-for-it -t 0 $POSTGRES_HOST:$POSTGRES_PORT -- java -jar stationmanager.jar