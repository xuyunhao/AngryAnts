#!/bin/csh -f

set file = $argv[1]

if ( -f $file ) then
    rm $file
endif

set size = $<

echo "1" >> $file
echo "0 0 0" >> $file
echo "0 0" >> $file
echo "0 0" >> $file
echo "2" >> $file
echo $size >> $file

set i = 0

while ($i < $size) 
    echo "$i $i " >> $file
    @ i++
end

set i = 0
set j = 5
echo $size >> $file
while ($i < $size)
    @ j += $i
    echo "$j $j" >> $file
    @ i++
end

set i = 0
set j = 10
echo $size >> $file
while ($i < $size)
    @ j += $i
    echo "$j $j" >> $file
    @ i++
end

set i = 0
set j = 10
set k = 5
echo $size >> $file
while ($i < $size)
    @ k = $j + $i
    @ j += $i
    echo "$k $k" >> $file
    @ i++
end
