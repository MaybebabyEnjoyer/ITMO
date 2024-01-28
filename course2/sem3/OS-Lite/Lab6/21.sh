#!/bin/bash

for N in {1..20}
do
    echo "N = $N" >> xd21.txt
    for z in {1..2}
    do
        python3 xdgen.py
        { time bash 3.sh "$N" >> xd21.txt ; } >> xd21.txt 2>&1
    done
done
