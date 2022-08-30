#!/bin/bash

wait-for-it -t 0 $POSTGRES_HOST:$POSTGRES_PORT
java -jar delivery-tracking.jar