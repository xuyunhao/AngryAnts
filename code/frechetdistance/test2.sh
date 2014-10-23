
for file in `ls ../../data/datatrees/data/` ; do
digit=`expr substr $file 5 6`
java Frechet ../../data/datatrees/data/${file} ../../data/medians/trees/median${digit}
done

