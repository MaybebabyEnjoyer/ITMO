#!/bin/bash

mkdir -p .trash
touch .trash.log

if [ -s .trash.log ]; then
  last=$(cat .trash.log | tail -1 | awk '{print $1}')
else
  last=0
fi

((last++))

if [ -d "$1" ]; then
  echo "$1 is a directory"
  exit
elif [ ! -f "$1" ]; then
  echo "No such file"
  exit
fi

ln -T "$1" .trash/"$last"
echo "$last" "$(realpath "$1")" >>.trash.log
rm "$1"
