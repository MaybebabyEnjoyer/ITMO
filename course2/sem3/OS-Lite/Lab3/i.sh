#!/bin/bash

function time_xd() {
    date +%d.%m.%Y_%H:%M:%S
}

mkdir test && echo "catalog test was created successfully" >> report
touch test/"$(time_xd)"
ping -c 1 www.net_nikogo.ru || echo "$(time_xd) error" >> report
