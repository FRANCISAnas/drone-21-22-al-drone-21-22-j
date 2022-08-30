#!/bin/bash
set -e
export COMPOSE_HTTP_TIMEOUT=180
docker network ls | grep team-j-created-network > /dev/null || docker network create --driver bridge team-j-created-network
docker-compose -f demo-docker-compose.yml up 

docker container rm --force $(docker container ls -a -f name=teamj-jolydrone* --quiet)

docker volume rm $(docker volume ls --quiet)