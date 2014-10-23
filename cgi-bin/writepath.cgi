#!/usr/bin/perl

###############################################################################
# writepath.pl, located in /cs/cgi/cgi-bin/projects/angryants
#
# Author: Paul Shen
# Reads completed path information sent from AntTracker app in HTTP POST form
# to stdin and relays that information to a text file named
#
# <name of video containing tracked ant>-(ant id).txt
# 
# in the directory 
# /cs/cgi/projects/angryants/paths/
###############################################################################

use CGI qw/:standard/;

print "Content-type: text/plain\n\n\n";

my $in_length = $ENV{CONTENT_LENGTH};
my $client_name = remote_host();
my $remote_name = remote_user(); # returns nothing right now...

print "\n$in_length\n";
print "Remote host name: ", "$client_name\n";
print "Authorization/verification name for remote user: ", "$remote_name\n"; 
print content_type(), "\n";

# read in unencoded POST data
my $in_data = param('POSTDATA');

# split input into parameters on delim '/'
my @params = split('/', $in_data);

# first param is host id: remote host name and mac addr
my $dir = "../../../projects/angryants/paths";
# first param is the name of the text file
my $fName = @params[0];

my $path = "${dir}/${fName}";

print "File path: $path\n";

# attempt to open/create file in append mode
if (!open(FILE, ">>", $path)) {
	print "Invalid file\n";
	die $!;	
}

# write remote host ip addr path name and click tuples to file
print FILE "@params[1] ", remote_addr(), "\n";

for ($i = 2; $i < scalar(@params); $i++) {
	print FILE "@params[$i]\n";
}

# attempt to close file, warn upon failure
close(FILE) or warn "file failed to close: $!\n";
			
exit 0;
