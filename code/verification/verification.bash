#!/bin/bash -u
#set -x

#range=$1

range=17
echo "================================" >> logfile
date >> logfile
echo "====== Verification Start ======" >> logfile
echo "" >> logfile
#for file in `ls /cs/cgi/cgi-bin/projects/angryants/php/data/*.txt`; do
for file in `ls saved/*.txt`; do
    echo $file >> logfile1
    echo $file
#    filename=${file:44}
#    echo $filename
    x=0
    for line in `cat $file`; do
        if [ $line == "processed" ]; then
            echo "processed already" >> logfile1
            x=1
        fi
    done
    if [ $x -eq 0 ]; then
        x=0
        str="java Verification ${file} ${range} 1"
        $str >> logfile
        x=$?

        if [ $x -eq 0 ]; then
            echo "save file" >> logfile1
            echo "mark to processed" >> logfile1
#           sed '1 i\processed' ${file} > ${file}.tmp
#           mv ${file}.tmp ${file}
#            cp $file $file.checked
# 	    mv $file saved/$filename
        elif [ $x -eq 1 ]; then
            echo "delete file" >> logfile1
#	    cp $file $file.checked
#            mv $file deleted/$filename
        elif [ $x -eq 2 ]; then
            echo "Do nothing waiting for updates" >> logfile1
        fi
    fi
    echo "" >> logfile
done
