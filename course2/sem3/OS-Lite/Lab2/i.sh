#!/bin/bash

output_file="out1.txt"
user=$(whoami)
processes=$(ps x -u "${user}")

echo "${processes}" | awk "END { print NR-1 }" >$output_file
echo "${processes}" | awk '{ if (NR != 1) { print $1, $5 } }' >>$output_file
