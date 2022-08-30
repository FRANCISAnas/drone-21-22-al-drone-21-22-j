#!/bin/bash

wait-for-it -t 0 $RABBIT_HOST:5672
wait-for-it -t 0 $POSTGRES_HOST:$POSTGRES_PORT -- java -jar flightmonitor.jar