#!/bin/bash

for (( i = 0; i < $1; i++));
do
    ./newxd.sh "$2" &
    sleep 0.3
done
