#!/bin/bash

until [ $(curl --silent $BANK_HOST:$BANK_PORT/status | grep READY -c ) == 1 ]
do
  echo "Bank Not Ready"
  sleep 1
done

wait-for-it -t 0 $POSTGRES_HOST:$POSTGRES_PORT -- java -jar customercare.jar