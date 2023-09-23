#!/bin/bash

if [ $PWD == $HOME ]
then
    echo $HOME
    exit 0
else
    echo "Error"
    exit 1
fi