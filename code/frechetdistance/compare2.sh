for file in `ls ../../data/compare/tree/trajectory/` ; do
echo "${file}" >> output_trajectory
java Frechet ../../data/compare/tree/trajectory/${file} ./out >> output_trajectory
done
