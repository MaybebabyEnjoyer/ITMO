#!/bin/bash
N=$1
for i in $(seq 1 "$N"); do
    bash xd.sh i i &
done
wait
