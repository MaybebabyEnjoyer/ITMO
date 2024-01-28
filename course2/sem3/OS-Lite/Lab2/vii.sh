#!/bin/bash

output_file="out7"

for pid in $(ps -eo pid=); do
  if [ -d "/proc/$pid/" ]; then
    new_data=$(awk '/read_bytes/ {print $2}' <"/proc/$pid/io")
    echo "$pid" "$new_data"
  fi
done >"tmp.txt"

sleep 3

while read -r line; do
  pid=$(echo "$line" | awk '{print $1}')
  if [ -d "/proc/$pid/" ]; then
    data=$(echo "$line" | awk '{print $2}')
    echo "$pid" "$data"
    new_data=$(awk '/read_bytes/ {print $2}' <"/proc/$pid/io")
    ((result = new_data - data))
    echo "$result"
  fi

done <"tmp.txt" | sort -rnk2 | head -3 | tr ' ' ':' >>$output_file

rm "tmp.txt"
