package pogvue.datamodel.motif;

import pogvue.util.Format;

import java.io.PrintStream;

public final class TFMatrix {

  private final double[][] value;
  private final int rows;
  private final int cols;
  private Pwm pwm;
  
  private String name;
  private String id;
  private String acc;
  private String desc;
  private String cons;

  public TFMatrix(Pwm pwm) {
    this(pwm.getPwm(),pwm.getPwm().length/4,4);
  }
    
  public TFMatrix(double[]tmpvalue, int rows, int cols) {
    this.rows = rows;
    this.cols = cols;

    
    name = "";
    id   = "";
    acc  = "";
    desc = "";
    cons = "";
    
    value = new double[rows][cols];

    int i = 0;

    while (i < tmpvalue.length/4) {
      value[i][0] = tmpvalue[i*4+0];
      value[i][1] = tmpvalue[i*4+1];
      value[i][2] = tmpvalue[i*4+2];
      value[i][3] = tmpvalue[i*4+3];
      i++;
    }
    makePwm();
  }
  public TFMatrix(double[][] value, int rows, int cols) {
    this.rows = rows;
    this.cols = cols;
    this.value = value;
    
    name = "";
    id   = "";
    acc  = "";
    desc = "";
    cons = "";
    
    makePwm();
  }

  public String getAcc() {
    return acc;
  }
  public String getConsensus() {
	return cons;
    }
  public String getDesc() {
return desc;
	}
  public String getId() {
    return id;
  }
  public double getInformationContent() {
    if (pwm != null) {
      return pwm.getInformationContent();
    } 
    return 0;
  }
  public double getLogScore(String seq) {
    return pwm.getLogScore(seq);
  }
  public String getName() {
    return name;
  }
  public Pwm getPwm() {
    return pwm;
  }
  public int getRows() {
return rows;
	}
  public double[][] getValues() {
    return value;
  }
  public void makePwm() {
    double[] mat = new double[rows*cols];
    //System.out.println("Matrix " + rows + " " + cols);
    
    for (int i = 0; i < rows; i++) {
      int tot = 0;
      for (int j = 0; j < cols; j++) {
	mat[i*4+j] = value[i][j];
	tot += value[i][j];
      }
      if (tot > 0) {
	for (int j = 0; j < cols; j++) {
	  mat[i*4+j] /= tot;
	}
      }
    }	
    
    pwm = new Pwm(mat,name);
    
    //System.out.print("Logo ");
    //Pwm.printLogo(pwm.getPwm());	
    //System.out.println();
  }
    public void print(PrintStream ps) {
		
		  for (int i = 0; i < rows; i++) {
		    for (int j = 0; j < cols; j++) {
		Format.print(ps,"%8.2f",value[i][j]);
		    }
		    ps.println();
		  }
		}
    public void setAcc(String acc) {
		  this.acc = acc;
		}
    public void setConsensus(String cons) {
	this.cons = cons;
    }
    public void setDesc(String desc) {
	this.desc = desc;
    }
  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
    pwm.setName(name);
  }

}







