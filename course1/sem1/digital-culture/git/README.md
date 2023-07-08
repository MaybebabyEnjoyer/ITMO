# commit-one-file-staged

git commit A.txt

# ingonre-them

Создать gitignore
touch .gitignore
Затем добавить в .gitignore
*.exe
*.o
*.jar
libraries/
Добавить файл в гит, запушить

# merge-conflict

git merge another-piece-of-work
Открыть equation.txt и пофиксить конфликт
git add equation.txt
git commit

# save-your-work

git stash
Открыть bug.txt, пофиксить баг
git commit (bug.txt)
git stash apply
Открыть bug.txt, добавить требуемую строку
git add
git commit

# change-branch-history

git rebase hot-bugfix

# forge-date

git commit --amend --date="Введите дату" --no-edit

# fix-old-typo

git log
git rebase -i 'x^' (change pick to edit on wrong commit)
Открыть file.txt
git add file.txt
git commit --all --amend
git rebase --continue
Открыть file.txt
git commit --all
git rebase --continue

# commit-lost

git checkout (hash of Very important piece of work commit)
git branch --force commit-lost
git checkout (hash of Very important piece of work commit)
git verify commit-lost

# too-many-commits

git rebase -i (меняем pick у второго на squash)

# executable

git update-index --chmod+=x script.sh

# commit-parts

git add -p file.txt
git commit
git add -p file.txt
git commit

# invalid-order

git rebase -i HEAD~2
Поменять коммиты местами

# search-improved

vim bisect_git.sh
Код скрипта:
#!/bin/bash
git bisect start HEAD
git bisect bad
git bisect good x (где x - коммит 1.0)
git bisect run ./faulty-check
git bisect reset
./bisect_git
git push origin x:search-improved
