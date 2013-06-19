package SequenceFeature;


# Stripped down sequence feature - no db access, no assembly version,  no parsing etc

use utils::Argument qw(rearrange);


sub new {
    my $caller = shift;

    my $class = ref($caller) || $caller;


    my ($chr,$type1,$type2,$start,$end,$score,$strand,$phase,$hitid) = rearrange(['CHR',
										  'TYPE1',
										  'TYPE2',
										  'START',
										  'END',
										  'SCORE',
										  'STRAND',
										  'PHASE',
										  'HITID',
										 ],@_);


    
    if (defined($strand)) {
	if ( !($strand == 1)  && 
	     !($strand == -1) && 
	     !($strand == 0)) {
	    throw('-STRAND argument must be 1, -1, or 0');
	}
    }

    if (defined($start) && defined($end)) {
	if($end+1 < $start) {
	    throw('Start must be less than or equal to end+1');
	}
    }

    
    return bless({
	'chr'      => $chr,
	'type1'    => $type1,
	'type2'    => $type2,
	'start'    => $start,
	'end'      => $end,
	'score'    => $score,
	'strand'   => $strand,
	'phase'    => $phase,
	'hitid'    => $hitid,
		 }, $class);
}

sub chr {
    my $self = shift;
    $self->{'chr'} = shift if (@_);
    return $self->{'chr'};
}
sub start {
    my $self = shift;
    $self->{'start'} = shift if(@_);
    return $self->{'start'};
}
sub end {
    my $self = shift;
    $self->{'end'} = shift if(@_);
    return $self->{'end'};
}

sub type1 {
    my $self = shift;
    $self->{'type1'} = shift if(@_);
    return $self->{'type1'};
}

sub type2 {
    my $self = shift;
    $self->{'type2'} = shift if(@_);
    return $self->{'type2'};
}
sub score {
    my $self = shift;
    $self->{'score'} = shift if(@_);
    return $self->{'score'};
}
sub strand {
    my $self = shift;
    $self->{'strand'} = shift if(@_);
    return $self->{'strand'};
}
sub phase {
    my $self = shift;
    $self->{'phase'} = shift if(@_);
    return $self->{'phase'};
}
sub hitid {
    my $self = shift;
    $self->{'hitid'} = shift if(@_);
    return $self->{'hitid'};
}

sub hit_feature {
    my $self = shift;

    $self->{hitid}      = $f->chr if (@_);
    $self->{hitfeature} = $f      if (@_);
	
    return $self->{hitfeature};
}

sub to_string {
    my $self = shift;

    return $self->{chr} . "\t" . 
	$self->{type1}  . "\t" . 
	$self->{type2}  . "\t" . 
	$self->{start}  . "\t" .
	$self->{end}    . "\t" .
	$self->{score}  . "\t" .
	$self->{strand} . "\t" . 
	$self->{phase}  . "\t" . 
	$self->{hitid};
}

sub length {
    my ($self) = @_;

    return ($self->end-$self->start+1);
}
sub clone {
    my ($f) = @_;

    if (!$f->isa("SequenceFeature")) {
	print "ERROR:  Feature must be a sequence feature to clone\n";
	exit(0);
    }

    my $newf = new SequenceFeature(-chr      => $f->chr,
				   -type1    => $f->type1,
				   -type2    => $f->type2,
				   -start    => $f->start,
				   -end      => $f->end,
				   -score    => $f->score,
				   -phase    => $f->phase,
	);

    if ($f->hitid) {
	$newf->hitid($f->hitid);
    }

    if ($f->hit_feature) {
	# Do I want to clone this?
	$newf->hit_feature($f->hit_feature);
    }

    return $newf;
}

1;
