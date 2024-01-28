#!/bin/bash

a=()    

while :
do
    a+=(1 2 3 4 5 6 7 8 9 10)
    if [[ "${#a[*]}" -ge $1 ]];
    then
        exit 0
    fi
done
