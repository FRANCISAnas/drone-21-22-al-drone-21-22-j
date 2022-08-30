#!/bin/bash
set -e

cd delivery-tracking
mvn clean package -DskipTests
docker build -f src/docker/Dockerfile.local -t teamj-jolydrone-deliverytracker .

cd ../flightmonitor
mvn clean package -DskipTests
docker build -f src/docker/Dockerfile.local -t teamj-jolydrone-flightmonitor .

cd ../customercare
mvn clean package -DskipTests
docker build -f src/docker/Dockerfile.local -t teamj-jolydrone-customercare .

cd ../deliveryplanner
mvn clean package -DskipTests
docker build -f src/docker/Dockerfile.local -t teamj-jolydrone-deliveryplanner .

cd ../gateway
mvn clean package -DskipTests
docker build -f src/docker/Dockerfile.local -t teamj-jolydrone-gateway .

cd ../stationmanager
mvn clean package -DskipTests
docker build -f src/docker/Dockerfile.local -t teamj-jolydrone-stationmanager .

cd ../maintenance
mvn clean package -DskipTests
docker build -f src/docker/Dockerfile.local -t teamj-jolydrone-maintenance .

cd ../Drone/drone
mvn clean package -DskipTests
docker build -f Dockerfile -t teamj-jolydrone-drone .
docker build -f src/test/docker/benchmark.dockerfile -t teamj-jolydrone-benchmark-rabbitmq .

cd ../../mocks/deliveryplanner
mvn clean package
docker build -f src/docker/Dockerfile.local -t teamj-mocks-jolydrone-deliveryplanner .

cd ../../Bank
docker build -t teamj-jolydrone-bank .

cd ../postgresqldb
docker build -t jolydronepostgresdb .

cd ../init
docker build -t teamj-jolydrone-initializer .

cd ../integrations/demotest
docker build -t jolydronetestrunner .

cd ../../
