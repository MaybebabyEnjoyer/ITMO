#!/bin/bash

check_oom() {
  dmesg -C
  ./runxd.sh "$1" "$2"
  wait

  if dmesg | grep -q 'Out of memory: Killed process'; then
    return 1
  else
    return 0
  fi
}

binary_search() {
  local low=1
  local high=3300000
  local mid
  local best=1

  while [ $low -le $high ]; do
    mid=$(((low + high) / 2))

    echo "Testing with n = $mid"

    if check_oom "$1" $mid; then
      best=$mid
      low=$((mid + 1))
    else
      high=$((mid - 1))
    fi
  done

  echo "Maximum safe value close to 3300000 found: $best"
}

binary_search "$1"
