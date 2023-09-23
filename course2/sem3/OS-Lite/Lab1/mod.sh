#!/bin/bash

maxPoints=$(awk -F ',' 'NR>1{sum[$2]+=$3} END{for (i in sum) print sum[i]}' students.csv | sort -nr | head -1)

awk -F ',' -v max="$maxPoints" 'NR>1{sum[$2]+=$3} END{for (i in sum) if (sum[i] == max) print i}' students.csv