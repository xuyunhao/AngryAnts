#!/usr/bin/perl

###############################################################################
# assignAnt.cgi, located in /cs/cgi/cgi-bin/projects/angryants
#
# Author: Paul Shen
# Reads the cumulative number of ants recorded, n, from a file and sends it to 
# the client when the game is started or when the user decides to play again to
# identify the <video #, index of ant in list> pair as
# <(n/x)%y>, n%x> where x = # ants to track, y = # video segments
# (e.g.) given x = 10, y = 3 in our case, 
# n = 0: <0, 0>
# n = 1: <0, 1>
# n = 10: <1, 0> /* 10 ants tracked in FIRST video */
# n = 29: <2, 9> /* 30th ant TOTAL ant so far in 3 videos */
# n = 30: <1, 0> /* start over */
#	
###############################################################################

use CGI qw/:standard/;

print "Content-type: text/plain\n\n";

my $dir = "../../../projects/angryants/paths";
my $fName = "antCount.txt";
my $path = "${dir}/${fName}";

if (!open(FILE, "<",  $path)) {
	print "Invalid file\n";
	die $!;
}

while (<FILE>) {
	print $_;
#	$count = int $_;
#	$count = $count + 1;
}

#print $count;

close(FILE) or warn "file failed to close: $!\n";
#
#if (!open(FILE, ">",  $path)) {
#	print "Invalid file\n";
#	die $!;
#}
#print FILE "${count}";
#
#close(FILE) or warn "file failed to close: $!\n";

exit 0;
