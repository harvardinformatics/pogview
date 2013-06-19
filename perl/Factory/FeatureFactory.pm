package Factory::FeatureFactory;

use strict;

use FileHandle;


use SequenceFeature;



sub nextGFFFeature {
    my ($fh) = @_;

    return readGFFFeature(<$fh>);
}

sub readGFFFeature {
    my ($line) = @_;


    if (!defined($line)) {
	return;
    }

    chomp($line);

    my @f = split(/\t/,$line);
    
    my $f = new SequenceFeature(-chr    => $f[0],
				-type1  => $f[1],
				-type2  => $f[2],
				-start  => $f[3],
				-end    => $f[4],
				-score  => $f[5],
				-strand => $f[6],
				-phase  => $f[7]);
    
    if (scalar(@f) > 8) {
	$f->hitid($f[8]);
    }
    
    if (scalar(@f) > 9) {
	my $hitf = new SequenceFeature(-chr    => $f[8],
				       -start  => $f[9],
				       -end    => $f[10],
				       -strand => $f[11],
	    );
	
	$f->hit_feature($hitf);
    }
    
    return $f;
}

1;


