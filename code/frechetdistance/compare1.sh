for file in `ls ../../data/compare/tree/frechet/` ; do
echo "${file}" >> output_frechet
java Frechet ../../data/compare/tree/frechet/${file} ./out >> output_frechet
done
