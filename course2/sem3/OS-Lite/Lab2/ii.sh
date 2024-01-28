#!/bin/bash

output_file="out2.txt"
ps ax -o pid,command | grep "/sbin/" | awk '{ print $1 }' >$output_file
