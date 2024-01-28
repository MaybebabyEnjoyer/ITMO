#!/bin/bash

found=false
export found

while read -r LINE; do
  path=$(echo "$LINE" | awk '{$1=""}sub(FS, "")')
  name=$(basename "$path")
  dir=$(dirname "$path")
  curname=$(echo "$LINE" | awk '{print $1}')
  if [ "$name" = "$1" ]; then
    found=true
    echo "Restore file from $dir? [y, n]"
    while true; do
      read -r -u 1 ANS
      case $ANS in
      y)
        if [ ! -d "$dir" ]; then
          dir=$HOME
        fi
        if [ -f "$dir/$name" ]; then
          while true; do
            read -r -u 1 -p "such file exists in $dir, file will be restored with following name: " name
            if [ ! -f "$dir/$name" ]; then
              break
            fi
          done
        fi
        ln -T ".trash/$curname" "$dir/$name"
        rm ".trash/$curname"
        sed -i "/^$curname/d" ".trash.log"
        break
        ;;
      n)
        break
        ;;
      *)
        echo "only y or n"
        ;;
      esac
    done
  fi
done < <(cat .trash.log)

if [ ! $found = "true" ]; then
  echo "No file '$1' in trash"
fi
