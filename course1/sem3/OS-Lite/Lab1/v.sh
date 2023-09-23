#!/bin/bash

cat /mnt/d/OS-1/syslog | awk '{if ($2 == "INFO") print $0}' > info.log
