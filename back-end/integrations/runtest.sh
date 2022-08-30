#!/bin/bash


docker-compose up --abort-on-container-exit --exit-code-from testrunner

docker container rm jolydroneone teamj-jolydrone-postgresqldb customercarehost deliveryplannerhost gatewayhost flightmonitorhost stationmanagerhost jolydronebankhost jolydronetestrunnerhost



