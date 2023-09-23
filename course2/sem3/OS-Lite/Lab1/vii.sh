#!/bin/bash

grep -r -E -o -s -h --binary-files=without-match "[[:alnum:]]+@[[:alnum:]]+\.[[:alnum:]]+" /etc | tr '\n' ',' | sed 's/,$//' > emails.lst
