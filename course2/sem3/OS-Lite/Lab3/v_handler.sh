#!/bin/bash

mode="add"
result=1
generator_pid=$(cat .generator_pid)

while true; do
    read -r line < my_pipe
    case $line in
        "+")
            mode="add"
            ;;
        "*")
            mode="multiply"
            ;;
        "QUIT")
            echo "Bye!"
            kill "$generator_pid"
            exit 0
            ;;
        *)
            if [[ $line =~ ^-?[0-9]+$ ]]; then
                if [ "$mode" = "add" ]; then
                    result=$((result + line))
                else
                    result=$((result * line))
                fi
                echo "Current result: $result"
            else
                echo "Error: Invalid input."
                kill "$generator_pid"
                exit 1
            fi
            ;;
    esac
done
