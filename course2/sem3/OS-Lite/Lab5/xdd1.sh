#!/bin/bash

target="log_2"

pid=$(cat pid_mem)
pid2=$(cat pid1_mem)

while true; do
  a=$(top -b -n 1)
  b=$(grep "$pid" <<<"$a")
  b2=$(grep "$pid2" <<<"$a")
  if [[ -z $b && -z $b2 ]]; then
    break
  fi
  date +%d.%m.%Y\ %H:%M:%S >>$target
  awk '{print $0}' <<<"$a" | head -n 5 | tail -n 2 >>$target
  echo "$b" >>$target
  echo "$b2" >>$target
  awk '{print $0}' <<<"$a" | head -n 12 | tail -n 5 >>$target
  echo >>$target
  sleep 5
done

sudo dmesg | grep "xd.sh"
