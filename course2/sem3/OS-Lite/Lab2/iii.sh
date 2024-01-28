#!/bin/bash

ps aux --sort=start_time | tail -n 1 | awk '{print $2}'
