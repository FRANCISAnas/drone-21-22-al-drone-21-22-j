#!/bin/bash
set -e
cd "../Bank"
docker build -t teamj-jolydrone-bank .
cd "../customercare"
docker build -f Dockerfile.local -t teamj-jolydrone-customercare .
cd "../deliveryplanner"
docker build -t teamj-jolydrone-deliveryplanner .
cd "../flightmonitor"
docker build -t teamj-jolydrone-flightmonitor .
cd "../stationmanager"
docker build -t teamj-jolydrone-stationmanager .
cd "../integrations/demotest"
docker build -t jolydronetestrunner .
cd "../../postgresqldb"
docker build -t jolydronepostgresdb .
cd "../integrations"


