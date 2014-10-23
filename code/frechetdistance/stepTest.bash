#!/bin/bash -u
#############################################################
#   File: stepTest.bash
#
#
#############################################################
#set -x

inDir=$1
outDir=$2

if [ ! -d ${inDir} ]; then
    echo "input directory is not valid"
    exit 1
fi
if [ ! -d ${outDir} ]; then
    echo "output directory is not valid"
    exit 1
fi

if [ ${inDir:${#inDir}-1} == '/' ]; then
    inDir="${inDir:0:${#inDir}-1}"
fi

if [ ${outDir:${#outDir}-1} == '/' ]; then
    outDir="${outDir:0:${#outDir}-1}"
fi

for file in `/bin/ls ${inDir}`; do
    echo $file
    `java Frechet ${inDir}/${file} ${outDir}/${file} >> time_output`
done
