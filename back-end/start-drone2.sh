#!/bin/bash
set -e
docker network ls | grep team-j-created-network > /dev/null || docker network create --driver bridge team-j-created-network
docker-compose -f halfwaydrone-docker-compose.yml run --rm halfwaydrone