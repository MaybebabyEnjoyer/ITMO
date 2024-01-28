#!/bin/bash

dir=$(ls | grep -E "Backup\-[0-9]{4}\-[0-9]{2}\-[0-9]{2}")

if [ "$dir" = "" ]; then
  echo "No backups"
  exit
fi

rm -r "restore"
mkdir -p "restore"

ls "$dir" | grep -vE "*.[0-9]{4}\-[0-9]{2}\-[0-9]{2}\$" | xargs -I {} cp "$dir"/{} restore/{}
