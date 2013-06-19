package pogvue.datamodel;

import java.util.Hashtable;
import java.util.Vector;
import java.awt.Color;

public final class CytoBand {

  private final Color GPOS25  = Color.darkGray;
  private final Color GPOS50  = Color.darkGray.darker();
  private final Color GPOS75  = Color.darkGray.darker().darker();
  private final Color GPOS100 = Color.black;
  private final Color GNEG    = Color.lightGray;
  private final Color ACEN    = Color.pink;
  private final Color GVAR    = Color.pink;
  private final Color STALK   = Color.red;
  String chr;
  int    start;
  int    end;
  String name;
  String stain;

  public CytoBand(String chr, int start, int end, String name, String stain) {

    setChr(chr);
    setStart(start);
    setEnd(end);
    setName(name);
    setStain(stain);
  }

  public String getChr() {
    return chr;
  }
  public Color getColor() {
    if (stain.equals("gneg")) {
      return GNEG;
    } else if (stain.equals("gpos25")) {
      return GPOS25;
    } else if (stain.equals("gpos50")) {
      return GPOS50;
    } else if (stain.equals("gpos75")) {
      return GPOS75;
    } else if (stain.equals("gpos100")) {
      return GPOS100;
    } else if (stain.equals("acen")) {
      return ACEN;
    } else if (stain.equals("gvar")) {
      return GVAR;
    } else if (stain.equals("stalk")) {
      return STALK;
    }
    return Color.white;
  }
  public int getEnd() {
    return end;
  }
  public String getName() {
    return name;
  }
  public String getStain() {
    return stain;
  }

  public int getStart() {
    return start;
  }
  public void setChr(String chr) {
    this.chr = chr;
  }
  public void setEnd(int end) {
    this.end = end;
  }
  public void setName(String name) {
    this.name = name;
  }
  public void setStain(String stain) {
    this.stain = stain;
  }
  public void setStart(int start) {
    this.start = start;
  }

}



     
