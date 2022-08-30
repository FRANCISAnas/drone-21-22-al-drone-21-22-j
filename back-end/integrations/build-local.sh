#!/bin/bash
set -e

cd ../delivery-tracking
mvn clean package -DskipTests

cd ../flightmonitor
mvn clean package 

cd ../customercare
mvn clean package 

cd ../deliveryplanner
mvn clean package 

cd ../gateway
mvn clean package

cd ../stationmanager
mvn clean package 

cd ../maintenance
mvn clean package 

cd ../Drone/drone
mvn clean package

cd ../../mocks/deliveryplanner
mvn clean package
cd ../../
