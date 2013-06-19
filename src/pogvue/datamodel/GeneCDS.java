package pogvue.datamodel;


public final class GeneCDS {
  private final String id;
  public final int    cds_start;
  public final int    cds_end;
  private final String translation;
  private final String org;
  private final String div;
  private final String description;
  private final String comment;
  private final int    codon_start;
  private String sequence;

  public GeneCDS(String id,int cds_start,int cds_end, String translation, int codon_start, String org, String div, String description, String comment, String seq) {
    this.id          = id;
    this.cds_start   = cds_start;
    this.cds_end     = cds_end;
    this.translation = translation;
    this.codon_start = codon_start;
    this.org         = org;
    this.div         = div;
    this.description = description;
    this.comment     = comment;
    this.sequence    = seq;
  }
}
