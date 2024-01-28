#!/bin/bash

./xd.sh &
./xd.sh &
./xd.sh &

pid=$(pgrep -o -f xd.sh)
cpulimit -p $pid -l 10 &

pid=$(pgrep -n -f xd.sh)
kill $pid
