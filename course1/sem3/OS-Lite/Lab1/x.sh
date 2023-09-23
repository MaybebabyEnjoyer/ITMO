#!/bin/bash

man bash | grep -o '\w\{4,\}' | sort | uniq -c | sort -nr | head -n 3