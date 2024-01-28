#!/bin/bash

output_file="out4.txt"

for pid_dir in /proc/[0-9]*; do
  if [ -f "$pid_dir"/status ]; then
    ppid=$(awk '/^PPid:/ {print $2}' "$pid_dir"/status)
  fi

  if [ -f "$pid_dir"/sched ]; then
    sum_exec_runtime=$(awk '/^se.sum_exec_runtime/ {print $3}' "$pid_dir"/sched)
    nr_switches=$(awk '/nr_switches/ {print $3}' "$pid_dir"/sched)
    if [[ "$sum_exec_runtime" =~ ^[0-9]+([.][0-9]+)?$ ]] && [[ "$nr_switches" =~ ^[0-9]+$ ]] && [ "$nr_switches" != "0" ]; then
      raw_art=$(echo "scale=8; $sum_exec_runtime / $nr_switches" | bc -l)
      art=$(echo "$raw_art" | awk '{if ($1 < 1) printf "0%s", $1; else print $1}')
    else
      art=0
    fi
  fi

  echo "ProcessID=$(basename "$pid_dir") : Parent_ProcessID=$ppid : Average_Running_Time=$art" >>$output_file
done

sort -t '=' -k2n $output_file -o $output_file
