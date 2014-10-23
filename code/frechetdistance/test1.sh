
for file in `ls ../../data/datagenerate/` ; do
digit=`expr substr $file 5 8`
java Frechet ../../data/datagenerate/${file} ../../data/medians/generateddata/median${digit}
done

