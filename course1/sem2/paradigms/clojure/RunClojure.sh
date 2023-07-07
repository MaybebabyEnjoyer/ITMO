#!/bin/bash
java \
    --class-path "$(dirname "0")/lib/*" \
    clojure.main "$@"
