#!/bin/bash

until [ $(curl --silent $DELIVERY_PLANNER_HOST:$DELIVERY_PLANNER_PORT/actuator/health | grep UP -c ) == 1 ]
do
  echo "DELIVERY PLANNER is Not Ready"
  sleep 2
done



until [ $(curl --silent $STATION_MANAGER_HOST:$STATION_MANAGER_PORT/actuator/health | grep UP -c ) == 1 ]
do
  
  echo "STATION MANAGER is Not Ready"
  sleep 1
done

echo "**** Initializing ****"

python3 main.py

sleep infinity