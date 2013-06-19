package pogvue.datamodel.expression;


public class ExpressionLevel {

  String gene;
  String tissue;
  int    level;
  int    sortcol;

  String ensg;
  String type;
  String name;
  String pfam;
  String cluster;

  public ExpressionLevel(String gene, String tissue, int level) {
    this.gene   = gene;
    this.tissue = tissue;
    this.level  = level;
  }

  public String getCluster() {
    return cluster;
  }
  public String getEnsg() {
    return ensg;
  }
  public String getGene() {
    return gene;
  }
  public int getLevel() {
    return level;
  }
  public String getName() {
    return name;
  }
  public String getPfam() {
    return pfam;
  }
  public int getSortCol() {
    return sortcol;
  }
  public String getTissue() {
    return tissue;
  }
  public String getType() {
    return type;
  }
  public void setCluster(String cluster) {
    this.cluster = cluster;
  }

  public void setEnsg(String ensg) {
    this.ensg = ensg;
  }
  public void setLevel(int level) {
    this.level = level;
  }
  public void setName(String name) {
    this.name = name;
  }
  public void setPfam(String pfam) {
    this.pfam = pfam;
  }
  public void setSortCol(int col) {
    this.sortcol = col;
  }
  public void setType(String type) {
    this.type = type;
  }
    
}
