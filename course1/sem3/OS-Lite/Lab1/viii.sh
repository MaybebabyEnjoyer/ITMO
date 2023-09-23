#!/bin/bash

cat /etc/passwd | awk -F: '{print $1, $3}' | sort -k2 -n