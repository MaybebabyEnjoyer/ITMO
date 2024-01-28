#!/bin/bash

target="log_1"

pid=$(cat pid_mem)

while :; do
  a=$(top -b -n 1)
  b=$(grep "$pid" <<<"$a")
  if [[ -z $b ]]; then
    break
  fi
  date +%d.%m.%Y\ %H:%M:%S >>$target
  awk '{print $0}' <<<"$a" | head -n 5 | tail -n 2 >>$target
  echo "$b" >>$target
  awk '{print $0}' <<<"$a" | head -n 12 | tail -n 5 >>$target
  echo >>$target
  sleep 5
done

sudo dmesg | grep "xd.sh"
