#!/usr/bin/perl

$myStr = "-1 test1-1";
$myStr2 = "test1-1";

$myInt = int(substr($myStr, 0, 2));
$myInt2 = int(substr($myStr2, 0, 2));

print $myInt;
print "\n";
print $myInt2;

if ($myInt == -1) { print "1 failed\n"; }
if ($myInt2 == -1) { print "2 failed\n"; }
