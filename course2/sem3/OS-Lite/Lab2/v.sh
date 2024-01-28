#!/bin/bash

input_file="out4"
output_file="out5"

awk -F '[:=]' '
{
    if ($4 != prev_ppid && NR != 1) {
        print "Average_Running_Children_of_ParentID=" prev_ppid " is " sum / count;
        sum = 0;
        count = 0;
    }
    print $0;
    sum += $6;
    count++;
    prev_ppid = $4;
}
END {
    print "Average_Running_Children_of_ParentID=" prev_ppid " is " sum / count;
}' $input_file >$output_file
