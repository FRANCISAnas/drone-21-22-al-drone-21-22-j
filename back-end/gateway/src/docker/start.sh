#!/bin/bash

set -e

echo "hello from gateway"
wait-for-it -t 0 $POSTGRES_HOST:$POSTGRES_PORT -- java -jar gateway.jar
