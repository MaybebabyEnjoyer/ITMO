c=$1
for i in {1..100}
do
    for j in {1..100}
    do
        c=$(($c + $2))
    done
done
