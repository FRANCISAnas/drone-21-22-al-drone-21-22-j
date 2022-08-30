#!/bin/bash
set -e

cd ../Bank
docker build -t teamj-jolydrone-bank .

cd ../delivery-tracking
docker build -f src/docker/Dockerfile.local -t teamj-jolydrone-deliverytracker .

cd ../customercare
docker build -f src/docker/Dockerfile.local -t teamj-jolydrone-customercare .

cd ../deliveryplanner
docker build -f src/docker/Dockerfile.local -t teamj-jolydrone-deliveryplanner .

cd ../gateway
docker build -f src/docker/Dockerfile.local -t teamj-jolydrone-gateway .

cd ../flightmonitor
docker build -f src/docker/Dockerfile.local -t teamj-jolydrone-flightmonitor .

cd ../stationmanager
docker build -f src/docker/Dockerfile.local -t teamj-jolydrone-gateway .

cd ../delivery-tracking
docker build -f src/docker/Dockerfile.local -t teamj-jolydrone-delivery-tracking .

cd ../maintenance
docker build -f src/docker/Dockerfile.local -t teamj-jolydrone-maintenance .

cd ../Drone/drone
docker build -f Dockerfile -t teamj-jolydrone-drone .
docker build -f src/test/docker/benchmark.dockerfile -t teamj-jolydrone-benchmark-rabbitmq .
cd ..

cd ../postgresqldb
docker build -t jolydronepostgresdb .

cd ../init
docker build -t teamj-jolydrone-initializer .

cd ../integrations/demotest
docker build -t jolydronetestrunner .

cd ../../mocks/deliveryplanner
docker build -f src/docker/Dockerfile.local -t teamj-mocks-jolydrone-deliveryplanner .
cd ../../


