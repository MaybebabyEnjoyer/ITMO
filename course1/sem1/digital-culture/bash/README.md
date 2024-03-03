### find-exe

```shell
find -type f -perm /u=x
```

### word-count

```shell
unzip word-count.zip 
find _zOPCKtIsbz -name '*azwAd*' -exec cp "{}" lab1  \;
cd lab1
wc -w *
```

### mod-sort

```shell
cd lab1
unzip mod-sort.zip
find lab1 -type f -exec stat --format '%Y %n' {} + | sort -n | awk '{print $2}' | xargs cat | sha256sum
```

### phone-numbers
```shell
tr ' ' '\n' < phone-numbers > output
grep -E -o '^\+[1-9][0-9]{1,14}$|^[1-9][0-9]{1,14}$' output | wc -l
```

### wget-pdfs
```shell
find lab1 -exec sh -c 'file "{}" | grep -q "PDF"' \; -print > output
total_size=0
while IFS= read -r filename; do
    size=$(stat -c "%s" "$filename")
    total_size=$((total_size + size))
done < output
echo $total_siz
```
