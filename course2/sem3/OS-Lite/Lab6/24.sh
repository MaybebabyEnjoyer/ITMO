#!/bin/bash

for N in {1..20}
do
    echo "N = $N" >> xd24.txt
    for z in {1..3}
    do
        python3 xdgen.py
        { time bash 4.sh "$N" >> xd24.txt ; } >> xd24.txt 2>&1
    done
done
