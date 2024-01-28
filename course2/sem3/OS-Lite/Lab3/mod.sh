#!/bin/bash

current_pipe="my_pipe"

while true; do
    read -r input
    if [[ $input == "switch:"* ]]; then
        current_pipe=${input#switch:}
    else
        echo "$input" > "$current_pipe"
    fi
done
