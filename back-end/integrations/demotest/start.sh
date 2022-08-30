#!/bin/bash

until [ $(curl --silent $BANK_HOST:$BANK_PORT/status | grep READY -c ) == 1 ]
do
  echo "Bank is Not Ready"
  sleep 1
done

until [ $(curl --silent $CUSTOMER_CARE_HOST:$CUSTOMER_CARE_PORT/actuator/health | grep UP -c ) == 1 ]
do
  echo "CustomerCare is Not Ready"
  sleep 1
done

until [ $(curl --silent $DELIVERY_PLANNER_HOST:$DELIVERY_PLANNER_PORT/actuator/health | grep UP -c ) == 1 ]
do
  echo "DELIVERY PLANNER is Not Ready"
  sleep 1
done

until [ $(curl --silent $FLIGHT_MONITOR_HOST:$FLIGHT_MONITOR_PORT/actuator/health | grep UP -c ) == 1 ]
do
  echo "FLIGHT MONITOR is Not Ready"
  sleep 1
done

until [ $(curl --silent $STATION_MANAGER_HOST:$STATION_MANAGER_PORT/actuator/health | grep UP -c ) == 1 ]
do
  echo "STATION MANAGER is Not Ready"
  sleep 1
done

echo "Before python script"
python3 main.py

echo "*** Goodbye ***"



