#!/bin/bash

value=1

add() {
    value=$((value + 2))
    echo "Текущее значение: $value"
}

multiply() {
    value=$((value * 2))
    echo "Текущее значение: $value"
}

trap add USR1
trap multiply USR2
trap 'echo "Обработчик завершил работу по сигналу от другого процесса"; exit' SIGTERM

while true; do
    sleep 1
done
