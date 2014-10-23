#!/usr/bin/perl

###############################################################################
# writeAntCount.cgi, located in /cs/cgi/cgi-bin/projects/angryants
#
# Author: Paul Shen
# Write the ant count, n, to a file in /cs/cgi/projects/angryants/antCount.txt
###############################################################################

use CGI qw/:standard/;

print "Content-type: text/plain\n\n\n";

my $dir = "../../../projects/angryants/paths";
my $fName = "antCount.txt";
my $path = "${dir}/${fName}";

if (!open(FILE, ">",  $path)) {
	print "Invalid file\n";
	die $!;
}

print FILE "0";
print "success?\n";

close(FILE) or warn "file failed to close: $!\n";

exit 0;
