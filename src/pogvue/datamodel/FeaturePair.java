package pogvue.datamodel;

public class FeaturePair extends SequenceFeature{
  public static void main(String[] args) {
      FeaturePair fp  = new FeaturePair();
  }
  private SequenceFeature f1;

  private SequenceFeature f2;
  public FeaturePair() {
      this.f1 = new SequenceFeature();
      this.f2 = new SequenceFeature();
  }
  public FeaturePair(SequenceFeature f1, SequenceFeature f2) {
    this.f1 = f1;
    this.f2 = f2;
  }
  public String getAlignString() {
    return f1.getAlignString();
  }
  public int        getEnd() {
    return f1.getEnd();
  }
  public int        getHend() {
    return f2.getEnd();
  }
  public String getHitAlignString() {
    return f2.getAlignString();
  }
  public SequenceFeature getHitFeature() {
    return f2;
  }
  public String      getHitId() {
    return f2.getId();
  }
  public int        getHstart() {
    return f2.getStart();
  }
  public int         getHstrand() {
    return f2.getStrand();
  }
  public String      getId() {
    return f1.getId();
  }
  public double getPercentId() {
      return f1.getPercentId();
  }
  public double getPValue() {
      return f1.getPValue();
  }
  public SequenceFeature getQueryFeature() {
    return f1;
  }
  public double      getScore() {
    return f1.getScore();
  }

  public int        getStart() {
    return f1.getStart();
  }
  public int         getStrand() {
    return f1.getStrand();
  }
  public void invert() {
    SequenceFeature tmp = f1;
    f1 = f2;
    f2 = tmp;
  }
  public void setAlignString(String str) {
    f1.setAlignString(str);
  }
  public void        setEnd(int end) {
    f1.setEnd(end);
  }
  public void        setHend(int end) {
    f2.setEnd(end);
  }
  public void setHitAlignString(String str) {
    f2.setAlignString(str);
  }
  public void        setHitFeature(SequenceFeature feature) {
    this.f2 = feature;
  }

  public void        setHitId(String name) {
    f2.setId(name);
  }
  public void        setHstart(int start) {
    f2.setStart(start);
  }
  public void        setHstrand(int strand) {
    f2.setStrand(strand);
  }
  public void setId(String id) {
      f1.setId(id);
  }
  
  public void setPercentId(double pid) {
      f1.setPercentId(pid);
      f2.setPercentId(pid);
  }

  public void setPValue(double value) {
      f1.setPValue(value);
      f2.setPValue(value);
  }
  public void        setQueryFeature(SequenceFeature feature) {
    this.f1 = feature;
  }

  public void        setScore(double score) {
    f1.setScore(score);
      f2.setScore(score);
  }
  public void        setStart(int start) {
    f1.setStart(start);
  }
  public void        setStrand(int strand) {
    f1.setStrand(strand);
  }
  
  public String toGFFString() {
      String tmp = f1.toGFFString();

      tmp = tmp + "\t" + getHitId() + "\t" + getHstart() + "\t" + getHend() + "\t" + getPValue() + "\t" + getPercentId();

      return tmp;
  }
}
