#!/bin/bash

username="maybebabyenjoyer"

ps aux | grep '^'$username'.*[Zz]' | awk '{print $2}'
