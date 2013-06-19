
# Shamelessly stolen from the ensembl codebase and moved to 
# pogvue namespace
#
# EnsEMBL module for Bio::EnsEMBL::Utils::Exception
#
#

# Usage :

#            use utils::Argument qw(rearrange);
#            my ($adaptor, $dbID) = rearrange(['ADAPTOR', 'dbID'],@_);


use strict;
use warnings;

package utils::Argument;

use Exporter;

use vars qw(@ISA @EXPORT);

@ISA    = qw(Exporter);
@EXPORT = qw(rearrange);

sub rearrange {
  my $order = shift;

  # If we've got parameters, we need to check to see whether
  # they are named or simply listed. If they are listed, we
  # can just return them.
  return @_ unless (@_ && $_[0] && substr($_[0], 0,1) eq '-');

  # Convert all of the parameter names to uppercase, and create a
  # hash with parameter names as keys, and parameter values as values
  my $i = 0;
  my (%param) = map {if($i) { $i--; $_; } else { $i++; uc($_); }} @_;


  # What we intend to do is loop through the @{$order} variable,
  # and for each value, we use that as a key into our associative
  # array, pushing the value at that key onto our return array.
  return map {$param{uc("-$_")}} @$order;
}

1;


