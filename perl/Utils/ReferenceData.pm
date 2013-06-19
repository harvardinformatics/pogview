package ReferenceData;


my %codon;
my %residue;

$codon{"AAA"} = "K";
$codon{"AAG"} = "K";
$codon{"AAC"} = "N";
$codon{"AAT"} = "N";

$codon{"CAA"} = "E";
$codon{"CAG"} = "E";
$codon{"CAC"} = "H";
$codon{"CAT"} = "H";

$codon{"GAA"} = "Q";
$codon{"GAG"} = "Q";
$codon{"GAC"} = "D";
$codon{"GAT"} = "D";

$codon{"TAC"} = "Y";
$codon{"TAT"} = "Y";

$codon{"ACA"} = "T";
$codon{"ACG"} = "T";
$codon{"ACC"} = "T";
$codon{"ACT"} = "T";

$codon{"CCA"} = "P";
$codon{"CCG"} = "P";
$codon{"CCC"} = "P";
$codon{"CCT"} = "P";

$codon{"GCA"} = "A";
$codon{"GCG"} = "A";
$codon{"GCC"} = "A";
$codon{"GCT"} = "A";

$codon{"TCA"} = "S";
$codon{"TCG"} = "S";
$codon{"TCC"} = "S";
$codon{"TCT"} = "S";
$codon{"AGC"} = "S";
$codon{"AGT"} = "S";

$codon{"AGA"} = "R";
$codon{"AGG"} = "R";
$codon{"CGA"} = "R";
$codon{"CGG"} = "R";
$codon{"CGC"} = "R";
$codon{"CGT"} = "R";

$codon{"GGA"} = "G";
$codon{"GGG"} = "G";
$codon{"GGC"} = "G";
$codon{"GGT"} = "G";

$codon{"TGA"} = "*";
$codon{"TAA"} = "*";
$codon{"TAG"} = "*";

$codon{"TGG"} = "W";

$codon{"TGC"} = "C";
$codon{"TGT"} = "C";

$codon{"ATA"} = "I";
$codon{"ATC"} = "I";
$codon{"ATT"} = "I";

$codon{"ATG"} = "M";

$codon{"CTA"} = "L";
$codon{"CTG"} = "L";
$codon{"CTC"} = "L";
$codon{"CTT"} = "L";
$codon{"TTA"} = "L";
$codon{"TTG"} = "L";

$codon{"GTA"} = "V";
$codon{"GTG"} = "V";
$codon{"GTC"} = "V";
$codon{"GTT"} = "V";

$codon{"TTC"} = "F";
$codon{"TTT"} = "F";


$residue{"K"} = ["AAA","AAG","AAC","AAT"];
$residue{"Q"} = ["CAA","CAG","CAC","CAT"];
$residue{"H"} = ["CAC","CAT"];
$residue{"E"} = ["GAA","GAG"];
$residue{"D"} = ["GAC","GAT"];
$residue{"Y"} = ["TAC","TAT"];
$residue{"T"} = ["ACA","ACG","ACC","ACT"];
$residue{"P"} = ["CCA","CCG","CCC","CCT"];
$residue{"A"} = ["GCA","GCG","GCC","GCT"];
$residue{"S"} = ["TCA","TCG","TCC","TCT","AGC","AGT"];
$residue{"R"} = ["AGA","AGG","CGA","CGG","CGC","CGT"];
$residue{"G"} = ["GGA","GGG","GGC","GGT"];
$residue{"*"} = ["TGA","TAA","TAG"];
$residue{"W"} = ["TGG"];
$residue{"C"} = ["TGC","TGT"];
$residue{"I"} = ["ATA","ATC","ATT"];
$residue{"M"} = ["ATG"];
$residue{"L"} = ["CTA","CTG","CTC","CTT","TTA","TTG"];
$residue{"V"} = ["GTA","GTG","GTC","GTT"];
$residue{"F"} = ["TTC","TTT"];

1;
