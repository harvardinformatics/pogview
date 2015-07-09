package Maf2;

use vars qw(@ISA);
use strict;
use FileHandle;

#use Useful;

sub new {
    my ($class) = shift;

    my ( $reforg,$mafdir,$orgfile) = @_;
    
    my $self = {
	reforg  => $reforg,
	dir     => $mafdir,
	orgfile => $orgfile
    };

    bless $self, $class;

    $self->readOrgfile();

    return $self;

}

sub readOrgfile {
    my ($self) = @_;

    if (! -e $self->{orgfile}) {
	print "ERROR: Can't find orgfile [".$self->{orgfile}."]\n";
	exit;
    }

    my $infile = $self->{orgfile};

    my $fh = new FileHandle();
    $fh->open("<$infile");
    
    my @orgs;
    my %rorgs;

    # This is <ahem> somewhat sensitive to the readme text
    while (<$fh>) {
	my @f = split(' ',$_);
	my $shortname  = $f[2];
	my $commonname = $f[0];
	my $longname   = $f[1];
	
	if ($shortname ne "") {
	    push(@orgs,$shortname);
	    
	    $rorgs{$shortname} = $commonname;
	}
    }
    $fh->close();
    
    $self->{orgs}  = \@orgs;
    $self->{rorgs} = \%rorgs;
}

sub get_rorgs {
    my ($self) = @_;

    return %{$self->{rorgs}};
}
sub get_orgs {
    my ($self) = @_;

    return @{$self->{orgs}};
}
sub index {
    my ($self) = @_;

    my $indexdir = $self->{dir};

    my $dir = opendir(DIR,$indexdir);
    my @files = readdir(DIR);

    foreach my $file (@files) {
	if ($file =~ /\.maf$/) {
	    my $index   = $indexdir . "$file.index";
	    my $alnfile = $indexdir . $file;

            if (! -e $index) {
	    print STDERR "Align $alnfile $index\n";

	    my $fh = new FileHandle;
	    $fh->open("<$alnfile");

	    open(OUT,">$index");

	    my $line;
	    my $pos = tell($fh);

	    while ($line = <$fh>) {

		if ($line =~ /^a/) {
		    $pos = tell($fh) - length($line);
		    $line = <$fh>;

		    if ($line =~ /^s\t(\S+)\t(\S+)\t(\S+)/) {
			print OUT $2 . "\t" . $3 . "\t" . ($pos) . "\n";
		    } else {
			print "Line $line\n";
		    }

		}
		$pos = tell($fh);
	    }

	    close(IN);
	    close(OUT);
	    }
	}
    }
}

sub get_align {
    my ($self,$chr,$start,$end) = @_;
    
    #print "Chr $chr $start $end\n";
    #get_human_seq($chr,$start,$end);

    my $indexdir = $self->{dir};
    my $index = $indexdir . "$chr.maf.index";

    my $ass   = $self->{reforg};
    #print "ASSembly $ass\n";
    my %index;

    if (! -e $index) {
	print "No index file $index\n";
	return;
    }

    open(IN,"<$index");
    
    sysseek(\*IN, 0, 2);                     # Find filesize
    my $filesize = systell(\*IN);

    #print "File size is $filesize - finding pos for $start\n";

    # Find the nearest position in the index file for the start coord

    my ($fh,$alnpos,$startcoord,$endcoord)= $self->findpos(\*IN,0,$filesize,$start,0,0,$filesize);

    #print "Found $alnpos\n";

    my $fh2 = new FileHandle();

    $fh2->open("<$indexdir/$chr.maf");

    sysseek($fh2,$alnpos,0);

    $fh->close();

    my %piece;
    
    my $prev = $start-1;
    
    my %fullstr;

    my @orgs = @{$self->{orgs}};

    
    while (%piece = $self->read_piece($fh2)) {
	
	#print "\nOriginal piece (cf $start - $end)\n";
	#$self->print_piece(\%piece);
	#print "\n";
	
	my $h = $ass;
	
	my $piece_end   = $piece{$h}{end};
	my $piece_start = $piece{$h}{start};
	my $piece_len   = $piece{$h}{len};

	#print "End $piece_start $piece_end\n";

	if ($piece_end < $start) {
	    last;
	}
	if ($end < $piece_start) {
	    last;
	}
	
	# Different cases
	
	# --------------------------
	#           ^start
	# piece overlaps start
	
	# TRIM
	
	
	if ($piece_start < ($prev +1) || $piece_end > $end) {
	    my $trimstart = 0;
	    my $trimend   = $piece_len-1;
	    
	    if ($piece_start < ($prev+1)) {
		$trimstart = ($prev + 1) - $piece_start;
		#print "Trimming start by $trimstart\n";
	    }
	    if ($piece_end > $end) {
		#print "Piece length $piece_len\n";
		#print "End diff $piece_end - $end\n";
		$trimend = $end - $piece_start;
		#print "Trimming end to $trimend\n";
	    }
	    
	    my $tmppiece = $self->trim_piece(\%piece,$trimstart,$trimend,$h,$piece_start);
	    
	    %piece = %$tmppiece;
	
	    #undef(%$tmppiece);

	    #print_piece(%piece);
	    
	    $piece_start = $piece{$h}{start};
	    $piece_end   = $piece{$h}{end};
	}
	
	# ...........--------------
	#     ^ start
	#
	# There's a gap before the piece - 
	
	if ($piece_start > ($prev+1)) {
	    
	    my $padlen = $piece_start - $prev - 1;
	    my $padstr = '-' x $padlen;
	    my $hpadstr = 'N' x $padlen;

	   # print "PADDING $padlen\t$piece_start\n";
	    my @orgs = $self->get_orgs();

	    foreach my $org (@orgs) {
		if ($org eq $h) {
		    $fullstr{$org} .= $hpadstr;
		} else {
		    $fullstr{$org} .= $padstr;
		}
	    }
	    
	} 
	
	# Add in the section
	
	# Nibble off ends gaps if they exist on the reference sequence

	$piece{$h}{string} =~ s/\-*$//;

	#print "Ref " .  $piece{$h}{string} . "\n";
	my $reflen = length($piece{$h}{string});	

	my $padlen = length($piece{$h}{string});
	my $padstr = '-' x $padlen;

#	print "Ref " . $piece{$h}{string} . " " . $reflen . "\n";

	foreach my $org (@orgs) {
	    if (defined($piece{$org}{string})) {
		$fullstr{$org} .= substr($piece{$org}{string},0,$reflen);
	#	print "Full " . $fullstr{$org} . "\n";
		#if (length($padstr) != length($piece{$org}{string})) {
		#    print "EEEEK! lengths not the same\n";
		#}
	    } else {
		$fullstr{$org} .= $padstr;
	    }
	}

#	print "Final piece $piece_end\n";
#	print_piece(%piece);
	# Set prev
	
	$prev = $piece_end;

	undef(%piece);
    }
    
    # Trim or pad end.
    
    if (($end - $prev) > 0) {
	my $padlen = $end-$prev;
	my $padstr = '-' x $padlen;
	
	foreach my $org (@orgs) {
	    $fullstr{$org} .= $padstr;
	}
    }
    $fh2->close();

    return %fullstr;
    
}

	
sub get_ref_seq {
    my ($self,$chr,$start,$end) = @_;

    $start--;

    # This isn't going to work
    my $cmd = "nibFrag -masked -name=$chr.$start-$end /ahg/scr3/mammals/local_data/hg18_seq/$chr.nib $start $end + stdout |";
    #print STDERR "Command $cmd\n";

    my $fh = new FileHandle();
    $fh->open("$cmd");

    my $seq;

    while (<$fh>) {
	if ($_ !~ /^>/) {
	    chomp;
	    $seq .= $_;
	}
    }
    return $seq;
}

sub findpos { 
    my ($self,$fh,$startpos,$endpos,$coord,$oldcoord,$hops,$filesize) = @_;

    #print "\n\nFinding index file position for coord $coord  between $startpos - $endpos\n";

    if (abs($startpos - $endpos) <= 1) {
	return $fh,$startpos,$coord,$coord;
    }

    my $halfpos   = int(($endpos+$startpos)/2);

    #print "Checking mid position $halfpos\n";

    my ($alnpos,$halfstart,$halfend) = $self->find_coord($fh,$halfpos,$filesize);

    #print "Coordinates at the halfway position are $halfstart - $halfend\n";
    
    if ($coord >= $halfstart && $coord <= $halfend) {
	return $fh,$alnpos,$halfstart,$halfend;
    } elsif ($halfstart > $coord) {

	#print "Recursing in lower half\n";

	if ($hops < 100) {
	    $hops++;
	    $self->findpos($fh,$startpos,$halfpos,$coord,$halfstart,$hops,$filesize);
	} else {
	    return;
	}
	
    } elsif ($halfend < $coord) {

#	print "Recursing in upper half\n";

	if ($hops < 100) {
	    $hops++;

	    $self->findpos($fh,$halfpos,$endpos,$coord,$halfend,$hops,$filesize);
	} else {
	    return;
	}
    }
}


sub find_coord {
    my ($self,$fh,$halfpos,$filesize) = @_;

    sysseek($fh,$halfpos,0);

    $fh = $self->backup_line($fh);

    my $startcoord;
    my $endcoord;
    my $alnpos;

    my $pos = systell($fh);

    my $prepos = $pos;

    my $c;

    sysread($fh,$c,1);

    while ($c ne "\t") {
	#print "got $pos :$c:\n";
	$startcoord .= $c;
	$pos++;
	if ($pos >= $filesize) {
	   return;
	}
	sysseek($fh,$pos,0);
	sysread($fh,$c,1);
    }

    $pos++;

    sysread($fh,$c,1);
    
    while ($c ne "\t") {
	#print "got coord :$c:\n";
	$endcoord .= $c;
	$pos++;
	if ($pos >= $filesize) {
	   return;
	}
	sysseek($fh,$pos,0);
	sysread($fh,$c,1);
    }

    $endcoord = $endcoord + $startcoord-1;
    $pos++;

    sysread($fh,$c,1);
    
    while ($c ne "\n") {
	#print "got coord :$c:\n";
	$alnpos .= $c;
	$pos++;
	if ($pos >= $filesize) {
	   return;
	}
	sysseek($fh,$pos,0);
	sysread($fh,$c,1);
    }

    sysseek($fh,$prepos,0);

    return ($alnpos,$startcoord,$endcoord);
}

	

sub systell {
    my ($self) = @_;
    use Fcntl 'SEEK_CUR';
    sysseek($_[0], 0, SEEK_CUR);
}

sub backup_line {
    my ($self,$fh) = @_;

    my $pos = systell($fh);

    my $c;

    sysread($fh,$c,1);
    
    #print "Pos $pos :$c:\n";
    while ($c ne "\n" && $pos >= 0) {
	$pos--;
	#print "Pos $pos :$c:\n";
	sysseek($fh,$pos,0);
	sysread($fh,$c,1);
    }

    $pos++;

    sysseek($fh,$pos,0);

    #print "Got beginning of line\n";

    return $fh;
}

sub strip_gapsC {
    my ($self,$seq1,$seq2) = @_;

    strip_gappedC($seq1,$seq2);

    return ($seq1,$seq2);
}

sub strip_gaps {
  my ($self,$seq1,$seq2) = @_;

  my @c1 = split(//,$seq1);
  my @c2 = split(//,$seq2);

  my $i = 0;

  my $newseq2;
  my $count = scalar(@c1);

  while ($i < $count) {

    if ($c1[$i] ne '-') {
       $newseq2 .= $c2[$i];
    }
    $i++;
 }
 return $newseq2;
}

sub read_piece {
    my ($self,$fh) = @_;

    #String s hg18.chr1    10999750 392 + 247249719 taggttctggcgtcaaactcctgggcccatactgtcctcctgcctcgaccccaatgtgctgagccaccatgcc-cagccACAATCTTGTTACCtttctttt

    my $line;
    my %piece;

    my $foundpiece = 0;

    while (($line = <$fh>) && ($foundpiece == 0 || $line ne "\n")) {
	#print "Piece $line\n";

	if ($line =~ /^s/) {
            $foundpiece = 1;
	    push(@{$piece{lines}},$line);
	 #   print $line;
	    my ($dum,$tmporg,$coord,$len,$strand,$chrlen,$string) = split(' ',$line);
	    
	    my $org;
	    my $chr;
	    
	    if ($tmporg =~ /(\S+)\.(\S+)/) {
		$org = $1;
		$chr = $2;
	    }
	    if ($dum eq "s") {
		$piece{$org}{chr}    = $chr;
		$piece{$org}{start}  = $coord+1;
		$piece{$org}{len}    = $len;
		$piece{$org}{end}    = $coord + $len;
		$piece{$org}{strand} = $strand;
		$piece{$org}{chrlen} = $chrlen;
		$piece{$org}{string} = $string;
	    }
	}
    }
    return %piece;
}


sub trim_piece {
    my ($self,$piece,$start,$end,$org,$piece_coord) = @_;

    # First find the string offsets in the human sequence (which will be gapped)

    #print "Trim start end $start $end\n";

    my $strstart;
    my $strend;

    my @c = split(//,$piece->{$org}{string});

    my $i      = 0;
    my $coord  = 0;

    my $piece_start = $piece_coord;
    my $piece_end;

    while ($i < scalar(@c)) {
	#print "Corrd $coord  " . scalar(@c) . " $i\n";

	if ($coord == $start) {
	    $strstart = $i;
	    $piece_start = $piece_coord;
	}
	if ($coord == $end) {
	    $strend    = $i;
	    $piece_end = $piece_coord;
	}

	if ($c[$i] ne '-') {
	    $coord++;
	    $piece_coord++;
	}
	$i++;
    }

    $piece_end = $piece_coord-1 unless $piece_end;
    $strend    = $i-1 unless $strend;

    #my %newstr;

    #print "Trimming a string "  . (length($piece->{hg18}{string})) . " long to $strstart $strend\n";
    #print "Trimming a string "  . (length($piece->{hg18}{string})) . " long to $piece_start $piece_end\n";

    my $len;

    foreach my $org (keys %$piece) {
	if ($org ne "lines") {
	    my $tmpstr = $piece->{$org}{string};
	    
	    $tmpstr = substr($tmpstr,$strstart,$strend-$strstart+1);
	    
	    $piece->{$org}{string} = $tmpstr;
	    #print "Piece " . $tmpstr . "\n";
	    if ($org =~ /^hg/) {
		$piece->{$org}{start} = $piece_start;
		$piece->{$org}{len}   = $piece_end-$piece_start;
		$piece->{$org}{end}   = $piece_end;
		
	    }
	    undef($tmpstr);
	}

    }
    return $piece;
}
sub print_piece {
    my ($self,$piece) = @_;

    my %piece = %$piece;
    my @orgs = $self->get_orgs();

    foreach my $org (@orgs) {

	if (defined($piece{$org})) {
	    printf("%12s\t%12d\t%12d\t%s\t%s\n", $org, $piece{$org}{start}, $piece{$org}{end}, substr($piece{$org}{string},0,40), substr($piece{$org}{string},-40,40));
	}
    }
}

sub copy_piece {
    my ($self,$p) = @_;

    my %p = %$p;
    my %newp;

    foreach my $key (keys %p) {

	if ($key ne "lines") {
	    my %f = %{$p{$key}};
	
	    foreach my $key2 (keys %f) {
		$newp{$key}{$key2} = $f{$key2};
	    }
	} else {

	    push(@{$newp{lines}},@{$p{lines}});
	}
    }

    return %newp;
}
	    

sub strip_str {
    my ($self,$fullstr) = @_;

    my %fullstr = %$fullstr;

    my $hstr = $fullstr{$self->{reforg}};
    my @hc   = split(//,$hstr);

    my %newstr;
    my @orgs = @{$self->{orgs}};


    foreach my $org (@orgs) {
	if ($org ne $self->{reforg}) {

	    my $tstr = $fullstr{$org};

	    my @tc   = split(//,$tstr);

	    my $i = 0;

	    my $newhstr;
	    my $newtstr;

	    while ($i < scalar(@hc)) {

		if ($hc[$i] ne '-' ) {
		    $newhstr .= $hc[$i];
		    $newtstr .= $tc[$i];
		}

		$i++;
	    }

	    $newstr{$org}{h} = $newhstr;
	    $newstr{$org}{t} = $newtstr;

	    undef($tstr);
	    undef(@tc);
	}
    }
    undef(%fullstr);
    return %newstr;
}


$ENV{'PERL_INLINE_DIRECTORY'}='/tmp';

use Inline C => << 'END_OF_C_CODE';

int strip_gappedC(SV* seq1, SV* seq2) {

   int i=0;
   int z=0;

   int seqlen1 = 0;
   int seqlen2 = 0;

   char *seqbuf1;
   char *seqbuf2;


  // ok set up and copy data from input to stuff we are going to munge on
  // we don\'t want to modify the perl data inplace in here, that would be bad

   seqlen1 = strlen(SvPV(seq1,PL_na)) + 1;
   seqlen2 = strlen(SvPV(seq2,PL_na)) + 1;


   seqbuf1 = malloc(sizeof(char)*seqlen1);
   seqbuf2 = malloc(sizeof(char)*seqlen2);


   strcpy(seqbuf1,SvPV(seq1, PL_na));
   strcpy(seqbuf2,SvPV(seq2, PL_na));


   // setup some space for the new buffer to return.
   // this will be the same size or smaller than the input string

   char *newseq1;
   char *newseq2;

   newseq1 = malloc(sizeof(char)*seqlen1);
   newseq2 = malloc(sizeof(char)*seqlen2);

//   fprintf(stderr, "Length of seq1 SV  = %d\n",strlen(SvPV(seq1, PL_na)));
 //  fprintf(stderr, "Length of seq2 SV  = %d\n",strlen(SvPV(seq2,PL_na)));
  // fprintf(stderr, "Length of seq1 (buffer) = %d\n",strlen(seqbuf1));
   //fprintf(stderr, "Length of seq2 (buffer) = %d\n",strlen(seqbuf2));


  // fprintf (stderr, "Inside C we have:\nseq1=%s\nseq2=%s\n",seqbuf1, seqbuf2);

   while (i < seqlen1) {
       if (seqbuf1[i] != '-') {
	   newseq1[z] = seqbuf1[i];
	   newseq2[z] = seqbuf2[i];
	   z++;
       }
       i++;
   }

   // this bit does the data copy for the return values

       sv_setpvn(seq1, newseq1, z);
   sv_setpvn(seq2, newseq2, z);

   free(seqbuf1);
   free(seqbuf2);

   free(newseq1);
   free(newseq2);

   return 1;
}

END_OF_C_CODE

1;
