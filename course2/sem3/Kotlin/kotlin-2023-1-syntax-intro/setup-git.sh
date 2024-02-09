#!/bin/bash

git update-index --skip-worktree .idea/externalDependencies.xml
git update-index --skip-worktree .idea/misc.xml
git update-index --skip-worktree .idea/workspace.xml
git update-index --skip-worktree .idea/gradle.xml

OIFS="$IFS"
IFS=$'\n'
for f in $(find . -name 'task-info.yaml' -type f); do git update-index --skip-worktree $f; done
for f in $(find . -name 'lesson-info.yaml' -type f); do git update-index --skip-worktree $f; done
