#!/bin/bash

wait-for-it -t 0 $RABBIT_HOST:$PORT
echo "Starting drone"
python3 main.py

echo "*** End drone ***"