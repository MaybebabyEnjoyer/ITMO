#!/bin/bash

echo $$ > .generator_pid

while true; do
    read -r input
    echo "$input" > my_pipe
done
