#!/bin/bash

touch "backup-report"

if [ ! -s "backup-report" ]; then
  last_backup=$(date -d "1970-01-01" +%F)
else
  last_backup=$(ls | grep -E "Backup\-[0-9]{4}\-[0-9]{2}\-[0-9]{2}" | tail -c 11)
fi

echo "$last_backup"
((dif = $(date +%s) - $(date -d "$last_backup" +%s)))
((delay = 7 * 24 * 60 * 60))

if [ "$dif" -gt "$delay" ]; then
  dir="Backup-$(date +%F)"
  echo "$(date +%F)" created new backup at "$dir" >>backup-report
else
  dir="Backup-$last_backup"
  echo "$(date +%F)" renewing backup "$dir" >>backup-report
fi

mkdir -p "$dir"

for FILE in source/*; do
  if [ ! -f "$dir/$(basename "$FILE")" ]; then
    cp "$FILE" "$dir/$(basename "$FILE")"
    echo "$(date +%F) new file \"$(basename "$FILE")\" in $dir" >>backup-report
  else
    if [ ! "$(wc -c "$FILE" | awk '{print $1}')" = "$(wc -c "$dir/$(basename "$FILE")" | awk '{print $1}')" ]; then
      mv "$dir/$(basename "$FILE")" "$dir/$(basename "$FILE").$(date +%F)"
      cp "$FILE" "$dir/$(basename "$FILE")"
      echo "$(date +%F)" renewing file "$(basename "$FILE")" with name "$(basename "$FILE")"."$(date +%F)" >>backup-report
    fi
  fi
done
