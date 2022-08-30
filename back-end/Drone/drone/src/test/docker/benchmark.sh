#!/bin/sh

echo "benchmark"
./wait-for-it.sh -t 0 $RABBIT_HOST:5672

mvn clean test -Pbenchmark