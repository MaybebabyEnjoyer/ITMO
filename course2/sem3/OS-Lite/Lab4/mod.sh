#!/bin/bash

SOURCE="xd1.exe"  
DEST="xd/cocopy.exe" 
BLOCK_SIZE=1048576  
PROGRESS_FILE="progress.txt"

update_progress() {
    total_size=$(stat --printf="%s" "$SOURCE")
    current_size=$(stat --printf="%s" "$DEST" 2>/dev/null || echo 0)
    progress=$(( 100 * current_size / total_size ))
    echo "Прогресс копирования: $progress%"
}

if [[ -f $PROGRESS_FILE ]]; then
    progress=$(cat $PROGRESS_FILE)
else
    progress=0
fi

update_progress

dd if="$SOURCE" of="$DEST" bs=$BLOCK_SIZE skip=$progress seek=$progress conv=notrunc,noerror,sync status=none

echo $(($progress + $(stat --printf="%s" "$DEST") / $BLOCK_SIZE)) > $PROGRESS_FILE
update_progress
