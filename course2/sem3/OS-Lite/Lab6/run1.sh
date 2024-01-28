for N in {1..20}
do
    echo "N = $N" >> c.txt
    for j in {1..10}
    do
        { time bash 1.sh "$N" >> c.txt ; } >> c.txt 2>&1
    done
done
