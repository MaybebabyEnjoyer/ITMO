#!/bin/bash

handler_pid=$1

while true; do
    read -r line
    case $line in
        "+")
            kill -USR1 "$handler_pid"
            ;;
        "*")
            kill -USR2 "$handler_pid"
            ;;
        "TERM")
            kill -SIGTERM "$handler_pid"
            echo "Пока-пока"
            exit
            ;;
        *)
            ;;
    esac
done
