
for file in `ls ../../data/datatrajectory/paths` ; do
digit=`expr substr $file 5 10`
java Frechet ../../data/datatrajectory/paths/${file} ../../results/medians/trajectory/frechet/median${digit}
done

