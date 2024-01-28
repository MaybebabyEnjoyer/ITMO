#!/bin/bash

max_memory=0
max_pid=0

for pid in /proc/[0-9]*; do
  if [ -f "$pid/status" ]; then
    memory=$(awk '/VmSize:/ {print $2}' "$pid/status")
    if [ -n "$memory" ] && [ "$memory" -gt "$max_memory" ]; then
      max_memory="$memory"
      max_pid=$(basename "$pid")
    fi
  fi
done

echo "The process with the most memory is PID=$max_pid using $max_memory."
