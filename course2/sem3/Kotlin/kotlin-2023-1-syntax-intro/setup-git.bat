@echo off
chcp 65001 > nul

git update-index --skip-worktree .idea/externalDependencies.xml
git update-index --skip-worktree .idea/misc.xml
git update-index --skip-worktree .idea/workspace.xml
git update-index --skip-worktree .idea/gradle.xml

for /R . %%f in (*task-info.yaml) do git update-index --skip-worktree "%%f"
for /R . %%f in (*lesson-info.yaml) do git update-index --skip-worktree "%%f"

