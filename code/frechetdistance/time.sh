#!/bin/csh -f

set targetDir=$argv[1]
set targetFile=$argv[2]
set size=10

if ( ! -d $targetDir ) then
    echo "input directory is not valid"
    exit 1
endif

echo "Initialize $targetFile"
if ( -f $targetFile ) then
    rm $targetFile
endif

echo "Computing ... "

foreach file($targetDir*)
    if ( -f $file ) then
        echo $file
        foreach i( 1 2 3 4 5 6 7 8 9 10 ) 
            echo $i
            java -Xmx1024m Frechet $file ./sample_out >> $targetFile
        end
    endif
end

