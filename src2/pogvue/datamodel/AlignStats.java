package pogvue.datamodel;

import java.util.*;

public class AlignStats {

  double[] cov;
  double[] pid;
  double[] bases;

  public AlignStats(double[] cov, double[] pid, double[] bases) {
    this.cov = cov;
    this.pid = pid;
    this.bases = bases;
  }

  public double[] getCoverage() {
    return cov;
  }
  public double[] getPID() {
    return pid;
  }
  public double[] getBases() {
    return bases;
  }
}