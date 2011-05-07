#!/usr/bin/perl -w
use strict;

#my $file = $ARGV[0];

#open(FILE, $file) or die "Can't open $file.";

my $willLines = 0;
my $samLines = 0;
my $colleenLines = 0;
my $evaLines = 0;
my $robLines = 0;

#while(my $line = <FILE>) {
while (my $line = <STDIN>) {
    if ($line =~ /Will Brown/) {
	$willLines += 1;
    } elsif ($line =~ /Colleen McKenzie/ || $line =~ /Я/) {
	$colleenLines += 1;
    } elsif ($line =~ /Samantha Ainsley/) {
	$samLines += 1;
    } elsif ($line =~ /Sam/) {
	$samLines += 1;
    } elsif ($line =~ /Eva Asplund/ || $line =~ /eva/) {
	$evaLines += 1;
    } elsif ($line =~ /Rob Post/) {
	$robLines += 1;
    } elsif ($line =~ /Rob/) {
	$robLines += 1;
    }
}

print "Totals: \n";
print "Will: $willLines\n";
print "Sam: $samLines\n";
print "Colleen: $colleenLines\n";
print "Eva: $evaLines\n";
print "Rob: $robLines\n";
