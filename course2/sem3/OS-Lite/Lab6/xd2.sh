#!/bin/bash

file="file$1.txt"

l=$(wc -l < "$file")

for ((i = 1; i <= l; i++)); do
    l1=$(sed -n ${i}p "$file")

    echo $((l1 * 2)) >> "$file"
done
