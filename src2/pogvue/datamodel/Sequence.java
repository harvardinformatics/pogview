package pogvue.datamodel;

import java.awt.*;


public class Sequence  {
  public  String   name;
  public  String   sequence;
  public  int      start;
  public  int      end;
  public  int      length;
  public  String   description;
  private String   displayId;

  private ChrRegion chr_region;

  public Sequence(Sequence seq) {
    this(seq.getName(),seq.getSequence(),seq.getStart(),seq.getEnd());
  }

  public Sequence(String name,String sequence) {
    this(name,sequence,1,sequence.length());
  }

  public Sequence(String name, String sequence, int start, int end) {
    this.name     = name;
    this.sequence = sequence;
    this.start    = start;
    this.end      = end;
    this.length   = sequence.length();

    setDisplayId();

  }



  public void setChrRegion(ChrRegion r) {
    this.chr_region = r;
  }
  public void setChrRegion(String chr, int start, int end, int strand) {
    this.chr_region = new ChrRegion(chr,start,end,strand);
  }
  public ChrRegion getChrRegion() {
    return chr_region;
  }
  public int getIdLength() {
    String id = name + "/" + start + "-" + end;
    return id.length();
  }
  public int findIndex(int pos) {

    // returns the alignment position for a base
    int j = start;
    int i = 0;

    while (i< sequence.length() && j <= end && j <= pos) {

      String s = sequence.substring(i,i+1);

      if (!(s.equals(".") || s.equals("-") || s.equals(" "))) {
        j++;
      }
      i++;
    }
    if (j == end && j < pos) {
      return end+1;
    } else {

      return i;
    }
  } 
  public int findPosition(int i) {
    // Returns the sequence position for an alignment position
    int j   = 0;
    int pos = start;

    while (j < i) {
      String s = sequence.substring(j,j+1);

      if (!(s.equals(".") || s.equals("-") || s.equals(" "))) {
        pos++;
      }
      j++;
    }
    return pos;
  }
  public char getCharAt(int i) {
    if (i < length) {
      return sequence.charAt(i);
    } else {
      return '-';
    }
  }
  public String getDescription() {
    return this.description;
  }
  public String getDisplayId() {
    return displayId;
  }
  public int getEnd() {
    return this.end;
  }
  public int getLength() {
    return this.length;
  }
  public String getName() {
    return this.name;
  }
  public String getSequence() {
    return this.sequence;
  }
  public String getSequence(int start,int end) {
    return this.sequence.substring(start,end);
  }
  public int getStart() {
    return this.start;
  }
  public String getSubstring(int i, int j) {
    return getSequence(i,j);
  }
  public void setDescription(String desc) {
    this.description = desc;
  }
  private void setDisplayId() {
    displayId = name;
  }
  public void setEnd(int end) {
    this.end = end;
    setDisplayId();
  }
  public void setName(String name) {
    this.name = name;
    setDisplayId();
  }

  public void setSequence(String seq) {
    this.sequence = seq;
    this.length   = seq.length();
  }

  public void setStart(int start) {
    this.start = start;
    setDisplayId();
  }
}
