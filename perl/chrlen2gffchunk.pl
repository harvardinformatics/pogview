#!/usr/bin/perl


$chunksize = 1000000;

while (<>) {
   chomp;
   my @f = split(/\t/,$_);

   my $chr = $f[0];
   my $len  = $f[1];


   $i = 1;

   while ($i < $len) {
      $end = $i + $chunksize-1;
      if ($end > $len) {
         $end = $len; 
      }
      print "$chr\tgenome\tgenome\t$i\t".($end) . "\t0\t1\t.\t$chr\n";

      $i += $chunksize;
  }
}
   
