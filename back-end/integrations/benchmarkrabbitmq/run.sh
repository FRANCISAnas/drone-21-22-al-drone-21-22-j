#!/bin/sh
export COMPOSE_HTTP_TIMEOUT=180

docker network ls | grep team-j-created-network > /dev/null || docker network create --driver bridge team-j-created-network
docker-compose -f docker-compose.yml up --abort-on-container-exit --exit-code-from benchmark

docker container rm --force $(docker container ls -a -f name=teamj-jolydrone* --quiet)

docker volume rm $(docker volume ls --quiet)