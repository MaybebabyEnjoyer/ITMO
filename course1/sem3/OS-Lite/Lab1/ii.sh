#!/bin/bash

str1=""
while [ "$str" != "q" ]
do
    read str
    str1="$str1$str"
done
echo $str1