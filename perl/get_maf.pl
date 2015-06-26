#!/usr/bin/perl

use strict;
use Cwd 'abs_path';
use File::Basename;

BEGIN {
  my $dir = dirname(dirname(abs_path($0))) . "/modules/";
  use FindBin;                 # locate this script
  use lib "$FindBin::Bin/../modules"; 
  use lib "$FindBin::Bin/"; 
}

use Maf2;
my $maf2 = new Maf2("strCam",
		   "/Users/mclamp/ratites/",
		   "/Users/mclamp/ratites/orgfile.dat");
#my $maf2 = new Maf2("hg19",
#		   "/Users/mclamp/multiz46way/maf/",
#		   "/Users/mclamp/multiz46way/orgfile.dat");

my $chr = "";
my $start = "";
my $end   = "";

if ($ARGV[0] =~ /(.*):(\d+)-(\d+)/) {
   $chr = $1;
   $start = $2;
   $end   = $3;
} else {
   $chr = $ARGV[0];
   $start = $ARGV[1];
   $end   = $ARGV[2];
}
my %aln = $maf2->get_align($chr,$start,$end);

my @orgs = $maf2->get_orgs();
foreach my $org (@orgs) {
    if (defined($aln{$org})) {
	print ">$org\n";
        my $str = $aln{$org};

        $str = $maf2->strip_gapsC($aln{$orgs[0]},$str);
	$str =~ s/(.{72})/$1\n/g;
	print $str . "\n";
    }
}

