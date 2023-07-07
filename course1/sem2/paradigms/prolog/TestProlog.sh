#!/bin/bash
set -euo pipefail

if [[ -z "$2" ]] ; then
    echo Usage: $(basename "$0") TEST-CLASS MODE VARIANT?
    exit 1
fi

CLASS="$1"
ARGS="$2 ${3-}"

OUT=__OUT
PROLOG="$(dirname "$0")"
REPO="$PROLOG/.."
LIB="$PROLOG/lib/*"

javac \
    -encoding utf-8 \
    -d "$OUT" \
    "--class-path=$LIB:$REPO/prolog:$REPO/java:$REPO/javascript:$REPO/clojure" \
    "$PROLOG/${CLASS//\.//}.java" \
 && java -ea "--class-path=$LIB:$OUT" "$CLASS" $ARGS
