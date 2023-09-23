#!/bin/bash

grep '\] (WW)' /mnt/d/OS-1/X.log | sed 's/(WW)/Warning:/' > full.log
grep '\] (II)' /mnt/d/OS-1/X.log | sed 's/(II)/Information:/' >> full.log
cat full.log