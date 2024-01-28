for N in {1..20}
do
    echo "N = $N" >> xd4.txt
    for j in {1..10}
    do
        { time bash 2.sh "$N" >> xd4.txt ; } >> xd4.txt 2>&1
    done
done
