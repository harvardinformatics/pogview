package Useful;

use SequenceFeature;
use FileHandle;

use strict;

my %factorial;


sub convgff {
    my ($fh) = @_;

    my @new;
    
    while (<$fh>) {
  my @f = split(/\t/);

  if ($f[0] =~ /(\S+)\.(\d+)-(\d+)/) {
      my $name  = $1;
      my $start = $2;
      my $end   = $3;

      $f[3] += $start - 1;
      $f[4] += $start - 1;
      $f[0] = $name;
  }

  if ($f[8] =~ /(\S+)\.(\d+)-(\d+)/) {
      my $name  = $1;
      my $start = $2;
      my $end   = $3;


      $f[9] += $start - 1;
      $f[10] += $start - 1;
      $f[8] = $name;
  }
  
  my $line = join("\t",@f);

  push(@new,$line);
    }
    return @new;
}

sub get_dinuc_count {
    my (@seq) = @_;
    
    my $topseq = $seq[0];
    
    my $i = 0;
    
    my %count;
    
    if (scalar(@seq) < 2) {
	return;
    }
    
    while ($i < $topseq->length) {
	
	my $dn0 = substr($topseq->seq,$i,2);
	my $dn1 = substr($seq[1]->seq,$i,2);
	
	$count{$dn0 . "." . $dn1}++;
	$i++;
	
    }
    return %count;
}

sub get_nuc_count {
    my (@seq) = @_;
    
    my $topseq = $seq[0];
    
    my $i = 0;
    
    my %count;
    
    if (scalar(@seq) < 2) {
	return;
    }
    
    while ($i < $topseq->length-1) {
	
	my $dn0 = substr($topseq->seq,$i,1);
	my $dn1 = substr($seq[1]->seq,$i,1);
	
	my $dn2 = substr($topseq->seq,$i+1,1);
        my $dn3 = substr($seq[1]->seq,$i+1,1);
	
	if ($dn0 eq 'C' && $dn2 eq 'G') {
	    $dn0 = 'D';
	}
	if ($dn1 eq 'C' && $dn3 eq 'G') {
	    $dn1 = 'D';
	}
	
	$count{$dn0 . "." . $dn1}++;
	$i++;
	
    }
    return %count;
}
  
sub read_gfffile {
    my ($file) = @_;

    my $fh = new FileHandle();
    $fh->open("<$file");
    
    return read_gff($fh);
}

sub read_gff_hash {
    my ($fh) = @_;

    my @gff = read_gff($fh);

    my %gff;

    foreach my $gff (@gff) {
	push(@{$gff{$gff->hseqname}},$gff);
    }

    return %gff;
}

    
sub read_gff {
    my ($fh) = @_;

    my @feat;

    while (<$fh>) {
  chomp;

  my $gff = read_gffline($_);

  if (defined($gff) && ($gff->isa("Bio::EnsEMBL::SeqFeature") ||
            $gff->isa("Bio::EnsEMBL::FeaturePair"))) {
      push(@feat,$gff);
      
#      if ($gff->start > 1000000) {
#    return @feat;
#      }
  }
    }

    return @feat;
}

sub read_gffline {
    my ($line) = @_;

    chomp($line);

    my $fp;

    eval {
	my %analysis;
	
	my @f = split(/\t/,$line);
	

	my $f1 = new Bio::EnsEMBL::SeqFeature;

        if ($f[6] > 0) {
	   $f[6] = 1;
	} elsif ($f[6] eq "+") {
	   $f[6] = 1;
       }elsif ($f[6] eq "-") {
	   $f[6] = -1;
       }
	$f1->seqname($f[0]);
	$f1->start($f[3]);
	$f1->end($f[4]);
	$f1->strand($f[6]);

	$f1->score($f[5]);
	$f1->{phase} = $f[7];
	$f1->{type1} = $f[1];
	$f1->{type2} = $f[2];
	$f1->{line} = $line;

	my $f2;

	if (!defined($analysis{$f[1]})) {
	    $analysis{$f[1]} = new Bio::EnsEMBL::Analysis(-logic_name => $f[1]);
	}

	if (scalar(@f) >= 9) {
	    $f2 = new Bio::EnsEMBL::SeqFeature;
	    
	    $f2->seqname($f[8]);
	    $f2->score($f[5]);
	    
	    if (scalar(@f) >= 11 && $f[10] !~ /coding/ && $f[9] =~ /\d+/) {
		$f2->start($f[9]);
		$f2->end($f[10]);
	    }
	    if (scalar(@f) >= 12) {
		$f2->strand($f[11]);
	    }
	    
	    if (scalar(@f) >= 13) {
		$f1->percent_id($f[12]);
	    }
	    
	    my $cig;
	    
	    if (scalar(@f) < 14) {
		$cig = $f1->length . "M";
	    } else {
		$cig = $f[13];
	    } 
	    
	    
	    $fp = new Bio::EnsEMBL::DnaDnaAlignFeature(-feature1 => $f1,
						       -feature2 => $f2,
						       -cigar_string => $cig);
	    
	    $fp->seqname($f1->seqname);
	    $fp->hseqname($f2->seqname);
	    $fp->score($f[5]);
            $fp->{line} = $line;
	    $fp->{phast} = $f1->{phase};
	    #   $fp->strand(1);
	    #   $fp->hstrand($f2->strand);
	    $fp->percent_id($f[12]);
	    $fp->analysis($analysis{$f[1]});
	    $fp->{type1} = $f[1];
	    $fp->{type2} = $f[2];
	    
	    if (scalar(@f) > 14) {
		
		$fp->{str1} = $f[14];
		$fp->{str2} = $f[15];
		
	    }
	    $fp->analysis($analysis{$f[1]});
	} else {
	    print "ERROR: Can't make feature pair from [$line] " . scalar(@f) . " [$@]\n";
	    foreach my $f (@f) {
	       print "Token\t" . $f . "\n";
	    }
	    $fp = $f1;
	    $fp->analysis($analysis{$f[1]});
	}
	
    };
    if ($@) {
	print "ERROR reading gfffile $line [$@]\n";
    }
    return $fp;

}


sub read_seq_hash {
    my ($seqfile) = @_;

    my @seqs = read_seq($seqfile);

    my %seq;

    foreach my $seq (@seqs) {
	$seq{$seq->id} = $seq;
    }

    return %seq;
}

sub read_seq_hash_fh {
    my ($fh) = @_;

    my @seqs = read_seq_fh($fh);

    my %seq;

    foreach my $seq (@seqs) {
	$seq{$seq->id} = $seq;
    }

    return %seq;
}

sub read_seq {
    my ($seqfile) = @_;

    open(IN,"<$seqfile") || die "Can't open seqfile $seqfile\n";

    return read_seq_fh(\*IN);
}

sub read_seq_fh {
    my ($fh) = @_;

    my @seq;

    my $seq;
    my $id;
    my $desc;
    my $seqstr;
    
    while (<$fh>) {
  chomp;
  if (/^>(.*)/) {

      my $tmpid = $1;
      my $tmpdesc;

      if ($tmpid =~ /(\S+) +(.*)/) {
	  $tmpid = $1;
	  $tmpdesc = $2;
      }

      if (defined($id)) {
    #if ($seqstr =~ /[ATGC]/) {

	  
	  if (length($seqstr) > 0) {
	      $seqstr =~ s/ //g;
	      $seqstr =~ s/\t//g;

	      $seq = new Bio::Seq(-id => $id, -seq => $seqstr, -desc => $desc, -type => 'dna');
	      push(@seq,$seq); 
	  }
	  #}
      }
      $id = $tmpid;
      $desc = $tmpdesc;
      $seqstr = "";
  } else {
      $seqstr .= $_;
  }
    }
    if (defined($id)) {
	if (length($seqstr) > 0) {
	      $seqstr =~ s/ //g;
	      $seqstr =~ s/\t//g;

      $seq = new Bio::Seq(-id => $id, -seq => $seqstr, -type => 'dna', -desc => $desc);
      push(@seq,$seq);
  }
    }
    
    close($fh);

    return @seq;
}


sub trim_gapped_align {
    my ($seq1,$seq2,$coord1,$len) = @_;

    my $i = 0;

    my @c1 = split(//,$seq1);
    my @c2 = split(//,$seq2);

    my $newseq1;
    my $newseq2;

    my $coord = -1;

    while ($i < scalar(@c1)) {

	if ($c1[$i] ne '-') {
	    $coord++;
	}
	
	if ($coord >= $coord1 &&
	    $coord < $coord1+$len) {
	    if ($c1[$i] ne '-') {
		$newseq1 .= $c1[$i];
		$newseq2 .= $c2[$i];
	    }
	}

	$i++;
    }

    return $newseq1,$newseq2;
}

sub trim_gapped_align_gapped {
    my ($seq1,$seq2,$coord1,$len) = @_;

    my $i = 0;

    my @c1 = split(//,$seq1);
    my @c2 = split(//,$seq2);

    my $newseq1;
    my $newseq2;

    my $coord = -1;

    
    while ($i < scalar(@c1)) {

	if ($c1[$i] ne '-') {
	    $coord++;
	}
	
	if ($coord >= $coord1 &&
	    $coord < $coord1+$len) {
	    $newseq1 .= $c1[$i];
	    $newseq2 .= $c2[$i];
	}

	$i++;
    }

    return $newseq1,$newseq2;
}

sub get_atg {
    my ($seq) = @_;

    if ($seq->seq eq "") {
  return -1,-1;
    }

    my @count = $seq->seq =~ /ATG/gx;
    my @revc  = $seq->revcom->seq =~ /ATG/gx;

    return scalar(@count),scalar(@revc);

}
sub get_motif {
    my ($seq,$motif) = @_;

    if ($seq->seq eq "") {
  return -1,-1;
    }

    my @count = $seq->seq =~ /$motif/gx;
    my @revc  = $seq->revcom->seq =~ /$motif/gx;

    return scalar(@count),scalar(@revc);

}

sub find_conserved {
    my (@seq) = @_;

    my @chars;

    foreach my $seq (@seq) {
  my @c = split(//,$seq->seq);
  push(@chars,\@c);
    }

    my $str;

    my $count = 0;

    while ($count < $seq[0]->length) {
  my $found = 1;
  my $i;
  foreach my $c (@chars)  {
      if (!defined($i)) {
    $i = $c->[$count];
      } else {
    if ($c->[$count] ne $i) {
        $found = 0;
    }
      }
  }
  if ($found == 1) {
      $str .= "*";
  } else {
      $str .= "-";
  }
  $count++;
    }
    return $str;
}
      
    
          
sub get_conserved_motif {
    my ($seq,$motif) = @_;

    my $topseq = shift @$seq;

    my $count;
    my $cons = 0;

    my @count = $topseq->seq =~ /$motif/gx;

    my $in = 0;
    my $str;
    my $prev;
    while (($in = index($topseq->seq,$motif,$in)) != -1) {

  my $found = 1;

  foreach my $s (@$seq) {
      if (substr($s->seq,$in,length($motif)) ne $motif) {

    $found = 0;
      }
  }
  if ($found == 1) {

      $cons++;
  }
  $in += length($motif);
    }

    my $newseq = $topseq->seq;
    
    $newseq = reverse($newseq);
    $newseq =~ tr/ATGC/TACG/;
    
    $newseq = new Bio::Seq(-seq => $newseq);
    
    my @revc  = $newseq =~ /$motif/gx;
    my $rcons = 0;

    my @newseq;

    foreach my $seq (@$seq) {
  my $tmpseq= $seq->seq;

  $tmpseq = reverse($tmpseq);
  $tmpseq =~ tr/ATGC/TACG/;
  
  push(@newseq,new Bio::Seq(-seq => $tmpseq));
    }

    $in = 0;

    while (($in = index($newseq->seq,$motif,$in)) != -1) {
  my $found = 1;

  foreach my $s (@newseq) {

      if (substr($s->seq,$in,length($motif)) ne $motif) {

    $found = 0;
      }
  }

  if ($found == 1) {

      $rcons++;
  }
  $in += length($motif);
    }

    return $cons,$rcons;

}
sub get_aligned_motif {
    my ($seq,$motif) = @_;

    my $topseq = shift @$seq;

    my $count;
    my $cons = 0;

    my @count = $topseq->seq =~ /$motif/gx;

    my $in = 0;
    my $str;
    my $prev;

    while (($in = index($topseq->seq,$motif,$in)) != -1) {

  my $found = 1;

  foreach my $s (@$seq) {
      if (substr($s->seq,$in,length($motif)) =~ /-/) {

    $found = 0;
      }
  }
  if ($found == 1) {
      #print "Found for at $in\n";

      $cons++;
  } 
      
  $in += length($motif);
    }

    my $newseq = $topseq->seq;
    
    $newseq = reverse($newseq);
    $newseq =~ tr/ATGC/TACG/;
    
    $newseq = new Bio::Seq(-seq => $newseq);
    
    my @revc  = $newseq =~ /$motif/gx;
    my $rcons = 0;

    my @newseq;

    foreach my $seq (@$seq) {
  my $tmpseq= $seq->seq;

  $tmpseq = reverse($tmpseq);
  $tmpseq =~ tr/ATGC/TACG/;
  
  push(@newseq,new Bio::Seq(-seq => $tmpseq));
    }

    $in = 0;

    while (($in = index($newseq->seq,$motif,$in)) != -1) {
  my $found = 1;

  foreach my $s (@newseq) {

      if (substr($s->seq,$in,length($motif)) =~ /-/) {

    $found = 0;

      }
  }

  if ($found == 1) {
      #print "Found rev at " . (length($newseq) - $in) . "\n";
      $rcons++;
  }
  $in += length($motif);
    }

    return $cons,$rcons;

}

sub print_align {
  my ($df,$slice1,$slice2,$gap1,$gap2,$pretty) = @_;

  my $dfstring;
  my $dfhstring;

  my $prev;
  my $hprev;


  if (defined($df->{str2})) {
    return $df->{str2};
  }

  foreach my $fp ($df->ungapped_features) {
      #print "coords " . $fp->start . "\t" . $fp->end . "\t" . $fp->length . "\n";

      my $tmpstr =  $slice1->subseq($fp->start-$df->start+1,$fp->end-$df->start+1);
      my $tmphstr;
      
      if ($df->hstrand == 1) {
    $tmphstr = $slice2->subseq($fp->hstart-$df->hstart+1,$fp->hend-$df->hstart+1);
      } else {
    my $tmp = new Bio::Seq(-seq => $slice2->subseq($fp->hstart-$df->hstart+1,$fp->hend-$df->hstart+1));
    $tmphstr = $tmp->revcom->seq;
      }
      
      if (defined($prev)) {
    if (($fp->start - $prev) == 1) {
        if ($gap2 == 1) {
      
      if ($fp->hstrand == 1) {
          my $pad = '-' x ($fp->hstart - $hprev- 1);
          $dfstring  .= $pad;
          
          my $hstr = $slice2->subseq($hprev - $df->hstart + 2,$fp->hstart - $df->hstart);
          $dfhstring .= $hstr;
      } else {
          my $pad = '-' x ($hprev - $fp->hend - 1);
          $dfstring  .= $pad;
          
          my $h = new Bio::Seq(-seq => $slice2->subseq($fp->hend - $df->hstart + 2,$hprev-$df->hstart));
          my $hstr = $h->revcom->seq;
          $dfhstring .= $hstr;
      }
        }
        $dfstring  .= $tmpstr;
        $dfhstring .= $tmphstr;
    } else {
        if ($gap1 == 1) {
      
      my $str = $slice1->subseq($prev - $df->start + 2,$fp->start - $df->start);
      
      $dfstring .= $str;
      
      my $pad = '-' x ($fp->start - $prev - 1);
      
      $dfhstring .= $pad;
        }
        $dfstring .= $tmpstr;
        $dfhstring .= $tmphstr;
    }
      } else {
    $dfstring = $tmpstr;
    $dfhstring = $tmphstr;  
      }
      
      $prev  = $fp->end;
      
      if ($fp->hstrand == 1) {
    $hprev = $fp->hend;
      } else {
    $hprev = $fp->hstart;
      }
      
  }

  if ($pretty) {
      my $pid = align2pretty(new Bio::Seq(-seq => $dfstring),
           new Bio::Seq(-seq => $dfhstring));
  }
  return ($dfhstring);
  
}

sub get_pid {
    my ($str1,$str2) = @_;

    my @c1 = split(//,$str1);
    my @c2 = split(//,$str2);

    my $match = 0;
    my $num   = 0;

    my $i = 0;

    while ($i < scalar(@c1)) {
	my $c1 = $c1[$i];
	my $c2 = $c2[$i];
	
	if ($c1 ne '-' && $c2 ne '-') {
	    $num++;

	    if ($c1 eq $c2) {
		$match++;
	    }
	}
	$i++;
    }
    
    if ($num > 0) {
	return int(100*$match/$num);
    } else {
	return 0;
    }
}

sub align2pretty {
  my ($seq1,$seq2,$line) = @_;
  
  my $str1 = $seq1->seq;
  my $str2 = $seq2->seq;

  $line = 60 unless $line;

  if (length($str1) != length($str2)) {
      print "ERROR: lengths of strings are different " . length($str1) . " " . length($str2) . "\n";
      #return;
  }
  
  my @c1 = split(//,$str1);
  my @c2 = split(//,$str2);

  my $alignstr;
  my $count = 0;
  my $countstr;

  my $pid      = 0;
  my $match    = 0;
  my $mismatch = 0;
  my $gap      = 0;

  my $prev;
  my $len;
  my $tot;

  my $num = 1;

  foreach my $c1 (@c1) {
    my $c2 = $c2[$count];

    $countstr .= $num;
    $num++;
    $num = $num % 10;

    if ($c1 eq $c2 && $c1 ne '-' && $c2 ne '-') {
      $alignstr .= "|";
      $match++;
      $tot++;
  } elsif ($c1 ne '-' && $c2 ne '-') {
      $mismatch++;
      $alignstr .= " ";
      $tot++;
    } else {
      $gap++;
      $alignstr .= " ";
    }

    $count++;
  }

  my $pid = 0;

  if ($tot > 0) {
      $pid = 100*$match/$tot;
  }
  
  print $seq1->id . " Percid $pid Match $match Mismatch $mismatch Gap $gap Total $count\n\n";


  my $idlen = length($seq1->id);
  
  if (length($seq2->id) > $idlen) {
    $idlen = length($seq2->id);
  }
  
  $idlen += 2;

  my $pad1 = ' ' x ($idlen - length($seq1->id));
  my $pad2 = ' ' x ($idlen - length($seq2->id));
  my $pad3 = ' ' x $idlen;

  my $id1 = $seq1->id . $pad1;
  my $id2 = $seq2->id . $pad2;
  my $id3 = $pad3;

  my $s1 = $seq1->seq;
  my $s2 = $seq2->seq;
  my $aid = ' ' x $idlen;

  $s1 =~ s/(.{$line})/$1\n/g;
  $s2 =~ s/(.{$line})/$1\n/g;
  $alignstr =~ s/(.{$line})/$1\n/g;
  $countstr =~ s/(.{$line})/$1\n/g;

  my @sp1 = split(/\n/,$s1);
  my @sp2 = split(/\n/,$s2);
  my @sp3 = split(/\n/,$alignstr);
  my @sp4 = split(/\n/,$countstr);

  my $i = 0;

  while ($i <= $#sp1) {
      print $id3 . $sp4[$i] . "\n";
      print $id1 . $sp1[$i] . "\n";
      print $aid . $sp3[$i] . "\n";
      print $id2 . $sp2[$i] . "\n\n";
   
	  $i++;
  }

  
  return $pid,$alignstr,$countstr;

}

sub print_gff {
    my ($f, $logic_name,$type,$cigar,$aln) = @_;

    my $str;
    
    $str =  $f->seqname . "\t$logic_name\t$type\t" . 
	$f->start  . "\t" . 
	$f->end    . "\t" . 
	$f->score  . "\t" . 
	$f->strand . "\t";

    if (defined($f->{phase})) {
	$str .= $f->{phase};
    } else {
	$str .= ".";
    }
    
    if ($f->isa("Bio::EnsEMBL::FeaturePair") && defined($f->hseqname)) {
	$str .= "\t" . 
	    $f->hseqname . "\t" . 
	    $f->hstart   . "\t" . 
	    $f->hend     . "\t" . 
	    $f->hstrand;
	
	if (defined($f->percent_id)) {
	    $str .= "\t" . $f->percent_id;
	}
	
	if ($cigar) {
	    if (defined($f->cigar_string)) {
		$str .= "\t" . $f->cigar_string;
	    }
	}
	if ($aln) {
	    if (defined($f->{str1})) {
		$str .= "\t" . $f->{str1};
	    }
	    if (defined($f->{str2})) {
		$str .= "\t" . $f->{str2};
	    }
	}
    }
    
    return $str;
}

sub print_net_gff {
    my ($f, $logic_name,$type,$cigar,$aln) = @_;

    my $str;
    
    $str =  $f->seqname . "\t$logic_name\t$type\t" . 
	$f->start  . "\t" . 
	$f->end    . "\t" . 
	$f->score  . "\t" . 
	$f->strand . "\t.";
    
    if ($f->isa("Bio::EnsEMBL::FeaturePair") && defined($f->hseqname)) {
	$str .= "\t" . 
	    $f->hseqname . "\t" . 
	    $f->hstart   . "\t" . 
	    $f->hend     . "\t" . 
	    $f->hstrand;
	
	if (defined($f->percent_id)) {
	    $str .= "\t" . $f->percent_id;
	}
	
	if ($cigar) {
	    if (defined($f->cigar_string)) {
		$str .= "\t" . $f->cigar_string;
	    }
	}
	if ($aln) {
	    if (defined($f->{str1})) {
		$str .= "\t" . $f->{str1};
	    }
	    if (defined($f->{str2})) {
		$str .= "\t" . $f->{str2};
	    }
	}

	$str .= $f->{next_score} . "\t" . $f->{contig_length};
    }
    
    return $str;
}

sub matrix_multiply {
    my ($f1,$f2) = @_;

    my @newmat;

    my $i = 0;


    while ($i < scalar(@$f1)) {
        my $j = 0;
        while ($j < scalar(@$f1))  {

            $newmat[$i][$j] = 0;

            my $ii = 0;

            while ($ii < scalar(@$f1)) {
                $newmat[$i][$j] += $f1->[$i][$ii]*$f2->[$ii][$j];
                $ii++;
            }
            $j++;
        }
        $i++;
    }
    return @newmat;
}

sub print_matrix {
    my (@mat) = @_;

    my $i = 0;

    while ($i < scalar(@mat)) {
        my $j = 0;

        while ($j < scalar(@mat)) {
            printf("%4.6e ",$mat[$i][$j]);
            $j++;
        }
        print("\n");
        $i++;
    }
    print "\n"; 

}

sub exponent {
    my (@mat) = @_;

    my $kk = 50;
    my $k  = 1;

    my @tmpmat = @mat;

    my @endmat;

    $endmat[0][0] = 1;
    $endmat[1][1] = 1;
    $endmat[2][2] = 1;
    $endmat[3][3] = 1;

    my @multmat = @endmat;

    while ($k < $kk) {
        @tmpmat = @mat;

        if ($k > 1) {
            @multmat = matrix_multiply(\@multmat,\@tmpmat);
        } else {
            @multmat = @mat;
        }

        @tmpmat = @multmat;

        my $multiplier = factorial($k);

        #print "Mult $k $multiplier\n";

        $multiplier = 1/$multiplier;

        constant_multiply($multiplier,@tmpmat);

        @endmat = matrix_add(\@tmpmat,\@endmat);

        #print "Current end matrix\n";

        #print_matrix(@endmat);

        $k++;
    }

    return @endmat;
}

sub logify {
    my ($i,@mat) = @_;

    my $kk = 50;
    my $k  = 1;

    my @tmpmat;
    my @multmat;

    $mat[0][0] -= 1;
    $mat[1][1] -= 1;
    $mat[2][2] -= 1;
    $mat[3][3] -= 1;

    my @endmat;

#    print "Mat - I\n";
#
#    print_matrix (@mat);

    while ($k < $kk) {

        if ($k > 1) {
            @multmat = matrix_multiply(\@multmat,\@mat);
            @tmpmat = @multmat;
        } elsif ($k == 1) {
            @multmat = @mat;
            @tmpmat  = @multmat;
        } else {
            $tmpmat[0][0] = 1;
            $tmpmat[1][1] = 1;
            $tmpmat[2][2] = 1;
            $tmpmat[3][3] = 1;
       }

        if ($i%2 == 1) {
           constant_multiply(-1,@tmpmat);
        }

        #print "Current mult matrix\n";
        
        #print_matrix(@multmat);
        @endmat = matrix_add(\@tmpmat,\@endmat);

        #print "Current end matrix\n";

        #print_matrix(@endmat);

        $k++;
    }

    return @endmat;
}


sub constant_multiply {
    my ($t,@f) = @_;

    my $i = 0;

    while ($i < scalar(@f)) {
        my $j = 0;
        while ($j < scalar(@f))  {
#           print "F " . $f[$i][$j] . " $i $j\n";
            $f[$i][$j] *= $t;

            $j++;
        }
        $i++;
    }
}

sub n_of_m {
    my ($n,$m) = @_;

    # n > m

    my $i = $n;

    my $tot = 1;
#    print "Pre $i $m $tot\n";

    if ($n - $m < $m) {
  while ($i > $m) {
      $tot = $tot * $i;
      $i--;
  }
  my $fac = Useful::factorial($n-$m);
  $tot = $tot/$fac;
#  print "Totty $tot\n";
  
#  print "Fac   $fac\n";

  return $tot;

    } else {
  while ($i > ($n-$m)) {
      $tot = $tot * $i;
      $i--;

  }
  my $fac = Useful::factorial($m);
  $tot = $tot/$fac;
#  print "Totty $tot\n";

#  print "Fac   $fac\n";
  
  return $tot;
    }
}

sub factorial {
    my ($num) = @_;

    if (!defined($factorial{$num})) {
        if ($num == 0) {
            $factorial{0} = 1;
        } else {
            my $i = 1;
            my $fac = 1;

            while ($i <= $num) {
                $fac *= $i;
                $i++;
            }

            $factorial{$num} = $fac;
        }
    }
 #   print "Fac " . $factorial{$num} . " $num\n";

    return $factorial{$num};
}
sub matrix_add {
    my ($f1,$f2) = @_;

    my $i = 0;

    my @newmat;

    my @f1;

    while ($i < scalar(@$f1)) {
        my $j = 0;
        while ($j < scalar(@$f1)) {
            $newmat[$i][$j] = $f1->[$i][$j] + $f2->[$i][$j];

            $j++;
        }
        $i++;

    }
    return @newmat;

}

sub overlap {
    my ($f1,$f2) = @_;

    my @starts = sort {$a <=> $b} ($f1->start,$f2->start);
    my @ends   = sort {$a <=> $b} ($f1->end,$f2->end);

    if ($starts[1] <= $ends[0]) {
	return ($ends[0] - $starts[1] + 1);
    }
}

sub rand_gauss {

    my ($mu,$sigma)=@_;

    my $z0=rand;

    my $z;

    if ($z0<0.5){
  $z=$z0;
    }
    else{
  $z=1-$z0;
    }
    
    my $t=sqrt(log(1/$z**2));
    
    my $c0 = 2.515517;
    my $c1 = 0.802853;
    my $c2 = 0.010328;
    my $d1 = 1.432788;
    my $d2 = 0.189269;
    my $d3 = 0.001308;
    
    my $x=$t-(($c0+$c1*$t+$c2*$t**2)/(1+$d1*$t+$d2*$t**2+$d3*$t**3));
    
    if ($z0 >= 0.5){
  $x=-$x;
    }
    
    $x=$mu+($sigma*$x);
    
    
    return $x;
    
}


sub get_gc {
    my ($str) = @_;

    $str =~ s/-//g;

    my $tmp = $str;

    $tmp =~ s/[GCgc]//g;

    if (length($str) > 0) {
    return int(100*(length($str)-length($tmp))/length($str));
    } else {
    return 0;
    }
}

sub find_frame_score {
    my ($aln1,$aln2,$window,$step,$htran) = @_;
   
    my @c1 = split(//,$aln1);
    my @c2 = split(//,$aln2);
    my @c3 = split(//,$htran);

    my @tot;
    my @totcount;

    my $i = 0;

    my $chunk;

    my $len;

    my $tmp = $aln1;

    $tmp =~ s/-//g;

    my $len = length($tmp);
    
    while ($i < $len) {

  my $foundstart == 0;

  $chunk++;

  my $j = 0;
  
  my @score;
  my $count;

  $score[0] = 0;
  $score[1] = 0;
  $score[2] = 0;

  my $frame1 = 0; 
  my $frame2 = 0;

  my $size = 100;
  
  if ($i+$size > $len) {
      $size = $len-$i;
  }

  my $coord = 0;

  while ($coord < $size) {

      my $c1 = $c1[$i+$j];
      my $c2 = $c2[$i+$j];

            my $c3 = $c3[$i+$j-1];
            my $c4 = $c3[$i+$j];

            # This is gap starting after an intron boundary

            if (($c3 eq ">" ||
     $c3 eq "<") &&
    $c2 eq "-") {

                $foundstart = 0;
            } 

            # This is gap ending before an intron boundary

            if (($c4 eq  ">" ||
     $c4 eq "<" ) && 
    $c2[$i+$j-1] eq "-") {

                $foundstart = 0;
            } 

      if ($foundstart == 0 && $c2 ne '-' && $c1 ne '-') {

    $frame2 = $frame1;
    $foundstart = 1;
      }

            if ($foundstart == 1) {
    if ($c1 eq "-") {
        $frame1++;
        $frame1 = $frame1%3;
    }

    if ($c2 eq "-") {
        $frame2++;
        $frame2 = $frame2%3;
    }
            }

      if ($c1 ne "-" &&
    $c2 ne "-") {

    $count++;

    if ($frame1 == $frame2) {
        $score[$frame1]++;
    }
      }

      $j++;

      #if ($c1 ne '-') {
    $coord++;
      #}

  }


  if ($count > 0) {
      $score[0] = int(100*$score[0]/$count);
      $score[1] = int(100*$score[1]/$count);
      $score[2] = int(100*$score[2]/$count);
  }

  push(@tot,\@score);
  push(@totcount,$count);

  $i += $step;
    }

    my $mav  = 0;
    my $mtot = 0;
    
    $i = 0;
  
    foreach my $score (@tot) {
  if ($totcount[$i] > 0) {
      $mav += $score->[0] + $score->[1] + $score->[2];
      $mtot++;
  }
  $i++;
    }
  
    return ($mav,$mtot);
}

sub get_match_string {
   my ($str1,$str2) = @_;

   my @c1 = split(//,$str1);
   my @c2 = split(//,$str2);

   my $str;

   my $i = 0;

   while ($i < scalar(@c1)) {

       if ($c1[$i] ne '-' &&
           $c2[$i] ne '-' &&
	   $c1[$i] eq $c2[$i]) {
	   $str .= "|";
       } else {
	   $str .= ' ';
       }

       $i++;
    }

    return $str;
}

sub strip_align {
   my ($str1,$str2) = @_;

   my @c1 = split(//,$str1);
   my @c2 = split(//,$str2);

   my $newstr1;
   my $newstr2;

   my $i = 0;
  
   while ($i < scalar(@c1)) {
      if ($c1[$i] ne '-') {
         $newstr1 .= $c1[$i];
         $newstr2 .= $c2[$i];
      }
      $i++;
   }
   return ($newstr1,$newstr2);
}

sub read_ucsc_genestart {
    my ($line) = @_;
    
    if ($line =~ /^\#/) {
	next;
    }
    
    
    my ($name,$chr,$strand,$gstart,$gend,$cdsstart,$cdsend,$exons,$starts,$ends,$id,$name2,$cdsStartStat,$cdsEndStat,$frames) = split(/\t/,$line);

    my %f;

    $f{chr}   = $chr;
    $f{name}  = $name;
    $f{start} = $gstart;
    $f{end}   = $gend;
    $f{id}    = $id;
    $f{strand} = $strand;
    $f{name2} = $name2;

    return %f;

    my @estarts = split(/,/,$starts);
    my @eends   = split(/,/,$ends);
    
    if ($cdsstart > 0 && $cdsend > 0) {
	
	my $i = 0;
	
	while ($i < $exons) {
	    
	    my $tmpstart = $estarts[$i];
	    my $tmpend   = $eends[$i];
	    
	    # Start exon
	    
	    if ($cdsstart >= $tmpstart && 
		$cdsstart <= $tmpend) {
		$tmpstart = $cdsstart;
		#   print "$chr\texon\texon\t$tmpstart\t$tmpend\t100\t$strand\t.\t$name\n";
	    }
	    
	    # End exon
	    
	    if ($cdsend >= $tmpstart &&
		$cdsend <= $tmpend) {
		$tmpend = $cdsend;
		#   print "$chr\texon\texon\t$tmpstart\t$tmpend\t100\t$strand\t.\t$name\n";
	    }
	    
	    if ($tmpstart >= $cdsstart && $tmpend <= $cdsend) {
		print "$chr\texon\texon\t$tmpstart\t$tmpend\t100\t$strand\t.\t$name\n";
	    }
	    $i++;
	}
    }
}

sub read_blatline {
    my ($line) = @_;

    chomp($line);

    my @f = split(/\t/,$line);

    my $bin        = $f[0];
    my $matches    = $f[1];
    my $mismatches = $f[2];
    my $repmatches = $f[3];
    my $ncount     = $f[4];
    my $q_n_insert = $f[5];
    my $q_b_insert = $f[6];
    my $t_n_insert = $f[7];
    my $t_b_insert = $f[8];
    my $strand     = $f[9];
    my $qname      = $f[10];
    my $qsize      = $f[11];
    my $qstart     = $f[12];
    my $qend       = $f[13];
    my $tname      = $f[14];
    my $tsize      = $f[15];
    my $tstart     = $f[16];
    my $tend       = $f[17];

    my $blocks     = $f[18];
    my $bsizestr   = $f[19];
    my $qstartstr  = $f[20];
    my $tstartstr  = $f[21];

    my @blocks  = split(",",$bsizestr);
    my @qstarts = split(",",$qstartstr);
    my @tstarts = split(",",$tstartstr);

    my %out;

    my $i = 0;

    my $offset = 0;

    while ($i < $blocks) {
	my $tmpqstart = $qstarts[$i];
	my $tmptstart = $tstarts[$i];
	my $bsize     = $blocks[$i];

	if ($i == 0) {
	    $offset = $tmptstart - $tstart;
	}
	my %tmpf;
	
	$tmpf{hstart}   = $tmpqstart,
	$tmpf{hend}     = $tmpqstart + $bsize;
	$tmpf{start}    = $tmptstart - $offset;
	$tmpf{end}      = $tmptstart - $offset + $bsize;
	
	$tmpf{seqname}  = $tname;
	$tmpf{hseqname} = $qname;
	
	if ($strand eq "+") {
	    $tmpf{strand} = 1;
	} else {
	    $tmpf{strand} = -1;
	}
	

	
	push(@{$out{$qname}},\%tmpf);
	
	$i++;
    }


    return \%out;
}


sub atoi {
   my $t;
   foreach my $d (split(//, shift())) {
      $t = $t * 10 + $d;
   }
   return $t;
}


1;
