package pogvue.analysis;

import java.util.*;
import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;
import pogvue.io.*;
import pogvue.util.*;


public class SearchTransfac {

  public static void searchMatrices(Pwm pwm, Vector mats, double thresh) {
    
    double[] pwm1 = pwm.getPwm();
    
    _searchMatrices(pwm,mats,thresh,1);
    
    pwm1 = Correlation4.revcompPwm(pwm1);
    
    Pwm newPwm = new Pwm(pwm1,pwm.getName());
    
    newPwm.setChrRegion(pwm.getChrRegion());
    
    _searchMatrices(newPwm,mats,thresh,-1);
  }
  
  public static void _searchMatrices(Pwm pwm, Vector mats,double thresh, int orient) {
    double[] pwm1   = pwm.getPwm();
    int      len1   = pwm1.length/4;
    
    for (int i = 0; i < mats.size(); i++) {

      TFMatrix tfm  = (TFMatrix)mats.elementAt(i);
      double[] pwm2 = tfm.getPwm().getPwm();
      int      len2 = pwm2.length/4;
      
      if (len2 > 6) {

	if (len1 < len2) {
	  
	  int j = 0;

	  while (j < len2-len1) {

	    double[] winpwm2 = Correlation4.substring(pwm2,j,j+len1-1);
	    double   tmpcorr = Correlation4.get(pwm1,winpwm2);

	    j++;

	    if (tmpcorr > thresh) {
		System.out.println("Correlation " + pwm.getName() + "\t" + pwm.getChrRegion() + "\t" + PwmCluster.getConsensus(pwm1) + "\t" + tfm.getName() + "\t" + tfm.getConsensus()  + "\t" + tmpcorr + "\t" + j + "\t" + orient);

	      // No GFF yet

	    }
	  }
	} else if (len1 > len2){
	  
	  int j = 0;

	  while (j < len1-len2) {

	    double[] winpwm1 = Correlation4.substring(pwm1,j,j+len2-1);
	    double   tmpcorr = Correlation4.get(winpwm1,pwm2);

	    j++;

	    if (tmpcorr > thresh) {
	      System.out.println("Correlation " + pwm.getChrRegion() + "\t" + PwmCluster.getConsensus(pwm1) + "\t" + tfm.getName() + "\t" + tfm.getConsensus() + "\t" + tmpcorr + "\t" + j + "\t" + orient);

	      ChrRegion reg    = pwm.getChrRegion();
	      int       start  = reg.getStart() + j;
	      int       end    = start + len2;

	      if (orient == -1) {
		end   = reg.getEnd() - j;
		start = end - len2;
	      }

	      System.out.println(reg.getChr() + "\t" +  tfm.getName()  + "\t" + tfm.getName() + "\t" + start + "\t" + end + "\t" + tmpcorr + "\t" + orient + "\t.\t" + tfm.getConsensus());

	    }
	  } 
	} else if (len1 == len2) {
	  double   tmpcorr = Correlation4.get(pwm1,pwm2);
	  if (tmpcorr > thresh) {
	    System.out.println("Correlation " + pwm.getChrRegion() + "\t" + PwmCluster.getConsensus(pwm1) + "\t" + tfm.getName() + "\t" + tfm.getConsensus() + "\t" + tmpcorr + "\t" + 0 + "\t" + orient);

	    // No GFF yet
	  }
	}
      }
    }
  }

  public static void print_help() {
    System.out.println("\nUsage: java pogvue.analysis.SearchTransfac -matrixfile <matfile> -transfacfile <tffile> -thresh <thresh> -tfformat <format> -name <matrixname>");

  }
  public static void main(String[] args) {
	
    try {
      
      Hashtable opts = GetOptions.get(args);
      
      if (opts.containsKey("-help")) {
        SearchTransfac.print_help();
        System.exit(0);
      }
      
      String    matfile  =   (String)opts.get("-matrixfile");
      String    tfcfile  =   (String)opts.get("-transfacfile");
      String    tfformat =   (String)opts.get("-tfformat");
      double    thresh   =   Double.parseDouble((String)opts.get("-thresh"));
      String    tfname   =   (String)opts.get("-name");
      
      if (matfile == null ||
          tfcfile == null ||
          thresh  <  0    ||
          thresh  >  1) {
        SearchTransfac.print_help();
        System.exit(0);
      }
      
      Vector matrices = null;

      // MatrixFactory here
      if (tfformat.equals("pwmline")) {
        PwmLineFile  pf      = new PwmLineFile(tfcfile,"File");	
        pf.parse();
        matrices = pf.getTFMatrices();
      } else if (tfformat.equals("transfac")) {
        TFMatrixFile tfm     = new TFMatrixFile(tfcfile,"File");
        Vector tmp = tfm.getMatrices();
        matrices = new Vector();
        
        if (tfname != null) {
          for (int i = 0;i < tmp.size(); i++) {
            String name  = ((TFMatrix)tmp.elementAt(i)).getName();
            
            if (name.indexOf(tfname) >= 0) {
              matrices.addElement(tmp.elementAt(i));
            }
          }
        } else {
          matrices = tmp;
        }
        
      } else {
        System.out.println("Unknown format [" + tfformat + "] known formats are transfac and pwmline");
        System.exit(0);
      }
      PwmLineFile  pf      = new PwmLineFile(matfile,"File");	

      Pwm pwm;
      
      System.out.println("MAtrices " + matrices.size());
      
      while ((pwm = pf.nextPwm()) != null) {
	  //System.out.println("Searching " + PwmCluster.getConsensus(pwm.getPwm()));
        SearchWorker sw = new SearchWorker(pwm,matrices);
        sw.setThreshold(thresh);
        sw.run();
        //SearchTransfac.searchMatrices(pwm,matrices,thresh);
      }
    } catch (Exception e) {
      System.out.println("\nException ");
      e.printStackTrace();
      
      print_help();
      System.exit(0);
    }
    
  }
}
