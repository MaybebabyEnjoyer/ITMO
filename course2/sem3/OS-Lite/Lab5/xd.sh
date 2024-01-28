#!/bin/bash

echo $$ >pid_mem

a=()
c=0

while :; do
  a+=(1 2 3 4 5 6 7 8 9 10)
  ((c++))
  if [[ $c -eq 100000 ]]; then
    echo "${#a[*]}" >>report.log
    c=0
  fi
done
