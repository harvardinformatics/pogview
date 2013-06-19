package pogvue.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

public class MatrixDisplay extends JPanel {

  private Vector<Vector<Double>> matrix;
  private Vector matrix2;
  private Vector<String> xlabels;
  private Vector<String> ylabels;
  private String legend;
  Vector labels;

  private boolean isRelative = false;

    private MatrixDisplay(Vector<Vector<Double>> matrix,Vector<String> xlabels, Vector<String> ylabels) {
	this.matrix  = matrix;
	this.xlabels = xlabels;
	this.ylabels = ylabels;
    }

  public MatrixDisplay(Vector<Vector<Double>> matrix) {
    this.matrix = matrix;
  }

    private void setLegend(String legend) {
	this.legend = legend;
    }

    private void setMatrix(Vector<Vector<Double>> d) {
	this.matrix = d;

	repaint();
    }

    public void setSecondMatrix(Vector d) {
	this.matrix2 = d;

	repaint();
    }

    public void paint(Graphics g) {
	Graphics2D screen2D = (Graphics2D)g;

        screen2D.setColor(Color.white);
        screen2D.fillRect(0, 0, getSize().width, getSize().height);

        screen2D.setColor(Color.red);

	screen2D.setFont(new Font("Helvetica",Font.PLAIN,6));

	double min = -1;
	double max = -1;



	for (int i = 0; i < matrix.size(); i++) {

	    Vector v = matrix.elementAt(i);

	    for (int j = 0; j < v.size(); j++) {

		double d = (Double) v.elementAt(j);
		double droot = Math.sqrt(d);

		if (min == -1 || droot < min) {
		    min = droot;
		}
		if (max == -1 || droot > max) {
		    max = droot;
		}

	    }
	}

	if (matrix2 != null) {
	    for (int i = 0; i < matrix2.size(); i++) {
		
		Vector v = (Vector)matrix2.elementAt(i);
		
		for (int j = 0; j < v.size(); j++) {
		    
		    double d = (Double) v.elementAt(j);
		    double droot = Math.sqrt(d);
		    
		    if (min == -1 || droot < min) {
			min = droot;
		    }
		    if (max == -1 || droot > max) {
			max = droot;
		    }
		    
		}
	    }
	}

	min -= (max-min)/10;
	max += (max-min)/10;

	int wspacing = size().width /(matrix.size()+1);
	int hspacing = size().height /(matrix.size()+1);

	System.out.println("Spacings " + hspacing +  " " + wspacing);

	for (int i = 0; i < matrix.size(); i++) {

	    Vector v = matrix.elementAt(i);
	    //Vector v2 = (Vector)matrix2.elementAt(i);
	    
	    //System.out.println("Row " + i);

	    double tot = 0;

	    for (int j = 0; j < v.size(); j++) {
	      double d = (Double) v.elementAt(j);
	      tot += Math.sqrt(d);
	    }

	    for (int j = 0; j < v.size(); j++) {
		double d = (Double) v.elementAt(j);
//		double d2 = ((Double)v2.elementAt(j)).doubleValue();



		if (isRelative) {
		  max = tot; 
		  d = Math.sqrt(d);
		} else {
		  d = Math.sqrt(d);
		}

		int radius = (int)(d * wspacing/(max));


		int x = (j+1)*wspacing;
		int y = (i+1)*hspacing - radius/2;
		
		Ellipse2D.Float circle = new Ellipse2D.Float(x, y, radius, radius);
		screen2D.fill(circle);

		//g.setColor(Color.red);

		//g.fillRect(x,wspacing/3,
		//(i+1)*hspacing -10,
		//10);
		//(int)(d*hspacing/(max)));
		
		//g.setColor(Color.blue);

		//g.fillRect(x+wspacing/3,wspacing/3,
		//(int)((i+1)*hspacing - d2*hspacing/(max)),
		//(int)(d2*hspacing/(max)));

	    }
	}

	screen2D.setColor(Color.black);
	
	for (int i = 0; i < xlabels.size(); i++) {
	    String label = xlabels.elementAt(i);

	    screen2D.drawString(label,(i+1)*wspacing,10);

	}

	for (int i = 0; i < ylabels.size(); i++) {
	    String label = ylabels.elementAt(i);

	    screen2D.drawString(label,5,(i+1)*hspacing);

	}

	screen2D.drawString(legend.substring(0,4),20,getSize().height - 10);

    }

    public static Vector<Vector<Double>> readMatrix(DataInputStream din) {
	String line;

	Vector<Vector<Double>> top = new Vector<Vector<Double>>();

	try {
	    while ((line = din.readLine()) != null) {
		if (line.equals("")) {
		    return top;
		}

		StringTokenizer str = new StringTokenizer(line);
		Vector<Double> tmp = new Vector<Double>();
	        double total = 0;
	
		top.addElement(tmp);
		
		while (str.hasMoreTokens()) {
		    double val = Double.parseDouble(str.nextToken());
                    total += val;
		    tmp.addElement(val);
		}
                for (int i = 0; i < tmp.size(); i++) {
                    double tmpel = tmp.elementAt(i) /total;
                    tmp.setElementAt(tmpel,i);
                }
               
	    }
	} catch (IOException e) {
	    System.out.println("Exception e " + e);
	}
	return top;
    }

  private static Vector<Vector<Double>> readSpliceMatrix(DataInputStream din) {
	String line;

	Vector<Vector<Double>> top = new Vector<Vector<Double>>();

	Vector<String> labels = new Vector<String>();
	labels.addElement("AA");
	labels.addElement("AC");
	labels.addElement("AG");
	labels.addElement("AT");
	labels.addElement("CA");
	labels.addElement("CC");
	labels.addElement("CG");
	labels.addElement("CT");
	labels.addElement("GA");
	labels.addElement("GC");
	labels.addElement("GG");
	labels.addElement("GT");
	labels.addElement("TA");
	labels.addElement("TC");
	labels.addElement("TG");
	labels.addElement("TT");


	int i = 0;
	while (i < labels.size()) {
	  Vector<Double> tmp = new Vector<Double>(labels.size());
	  int j = 0;
	  while (j < labels.size()) {
	    tmp.addElement((double) 0);
	    j++;
	  }
	  top.addElement(tmp);
	  i++;
	}

	Hashtable<String, Hashtable<String, Integer>> h2 = new Hashtable<String, Hashtable<String, Integer>>();
	int count = 0;
	try {
	  while ((line = din.readLine()) != null) {
	    if (line.equals("")) {
	      return top;
	    }
	    
	    StringTokenizer str = new StringTokenizer(line);

	    String human = str.nextToken();

	    if (labels.contains(human)) {	    

	      
	      Hashtable<String, Integer> h = new Hashtable<String, Integer>();
	      
	      while (str.hasMoreTokens()) {
		String dinuc  = str.nextToken();
		int    dcount = Integer.parseInt(str.nextToken());
		
		if (labels.contains(dinuc)) {
		  count += dcount;
		  h.put(dinuc, dcount);
		}
	      }
	      
	      h2.put(human,h);
	    }
	  }
	} catch (IOException e) {
	  System.out.println("Exception e " + e);
	}

	Enumeration<String> en2 = h2.keys();

	while (en2.hasMoreElements()) {
	  String human = en2.nextElement();

	  Hashtable h  = h2.get(human);
	  Vector<Double> tmp = (top.elementAt(labels.indexOf(human)));
	  
	  Enumeration en = h.keys();
	  
	  while (en.hasMoreElements()) {
	    String d = (String)en.nextElement();
	    int    c = (Integer) h.get(d);
	    
	    double p = c*1.0/count;
	    
	    tmp.setElementAt(p,labels.indexOf(d));
	  }
	  
	}
	for (i = 0; i < top.size(); i++) {
	  String human = labels.elementAt(i);
	  Vector tmp = top.elementAt(i);

	  System.out.print(human);

	  double total = 0;
	  for (int j = 0; j < tmp.size(); j++) {

	    String org = labels.elementAt(j);

	    double d   = (Double) tmp.elementAt(j);
	    total += d;


	  }

	  for (int j = 0; j < tmp.size(); j++) {

	    String org = labels.elementAt(j);

	    double d   = (Double) tmp.elementAt(j);
	    System.out.println("\t"  + org + "\t" + d/total);


	  }
	  System.out.println();
	}
	    
	return top;
  }

  private static Vector<Vector<Double>> readCodonMatrix(DataInputStream din) {
	String line;

	Vector<Vector<Double>> top = new Vector<Vector<Double>>();

	Vector<String> labels = new Vector<String>();
	labels.addElement("AAA");
	labels.addElement("AAC");
	labels.addElement("AAG");
	labels.addElement("AAT");
	labels.addElement("ACA");
	labels.addElement("ACC");
	labels.addElement("ACG");
	labels.addElement("ACT");
	labels.addElement("AGA");
	labels.addElement("AGC");
	labels.addElement("AGG");
	labels.addElement("AGT");
	labels.addElement("ATA");
	labels.addElement("ATC");
	labels.addElement("ATG");
	labels.addElement("ATT");
	labels.addElement("CAA");
	labels.addElement("CAC");
	labels.addElement("CAG");
	labels.addElement("CAT");
	labels.addElement("CCA");
	labels.addElement("CCC");
	labels.addElement("CCG");
	labels.addElement("CCT");
	labels.addElement("CGA");
	labels.addElement("CGC");
	labels.addElement("CGG");
	labels.addElement("CGT");
	labels.addElement("CTA");
	labels.addElement("CTC");
	labels.addElement("CTG");
	labels.addElement("CTT");
	labels.addElement("GAA");
	labels.addElement("GAC");
	labels.addElement("GAG");
	labels.addElement("GAT");
	labels.addElement("GCA");
	labels.addElement("GCC");
	labels.addElement("GCG");
	labels.addElement("GCT");
	labels.addElement("GGA");
	labels.addElement("GGC");
	labels.addElement("GGG");
	labels.addElement("GGT");
	labels.addElement("GTA");
	labels.addElement("GTC");
	labels.addElement("GTG");
	labels.addElement("GTT");
	labels.addElement("TAA");
	labels.addElement("TAC");
	labels.addElement("TAG");
	labels.addElement("TAT");
	labels.addElement("TCA");
	labels.addElement("TCC");
	labels.addElement("TCG");
	labels.addElement("TCT");
	labels.addElement("TGA");
	labels.addElement("TGC");
	labels.addElement("TGG");
	labels.addElement("TGT");
	labels.addElement("TTA");
	labels.addElement("TTC");
	labels.addElement("TTG");
	labels.addElement("TTT");

	int i = 0;

	while (i < labels.size()) {
	  Vector<Double> tmp = new Vector<Double>(labels.size());
	  int j = 0;
	  while (j < labels.size()) {
	    tmp.addElement((double) 0);
	    j++;
	  }
	  top.addElement(tmp);
	  i++;
	}

	Hashtable<String, Hashtable<String, Integer>> h2 = new Hashtable<String, Hashtable<String, Integer>>();
	int count = 0;
	try {
	  while ((line = din.readLine()) != null) {
	    if (line.equals("")) {
	      return top;
	    }
	    
	    StringTokenizer str = new StringTokenizer(line);

	    String human = str.nextToken();

	    if (labels.contains(human)) {	    

	      
	      Hashtable<String, Integer> h = new Hashtable<String, Integer>();
	      
	      while (str.hasMoreTokens()) {
		String dinuc  = str.nextToken();
		int    dcount = Integer.parseInt(str.nextToken());
		
		if (labels.contains(dinuc)) {
		  count += dcount;
		  h.put(dinuc, dcount);
		}
	      }
	      
	      h2.put(human,h);
	    }
	  }
	} catch (IOException e) {
	  System.out.println("Exception e " + e);
	}

	Enumeration<String> en2 = h2.keys();

	while (en2.hasMoreElements()) {
	  String human = en2.nextElement();

	  Hashtable h  = h2.get(human);
	  Vector<Double> tmp = (top.elementAt(labels.indexOf(human)));
	  
	  Enumeration en = h.keys();
	  
	  while (en.hasMoreElements()) {
	    String d = (String)en.nextElement();
	    int    c = (Integer) h.get(d);
	    
	    double p = c*1.0/count;
	    
	    tmp.setElementAt(p,labels.indexOf(d));
	  }
	  
	}
	return top;
  }


  private static Vector<String> getCodonLabels() {
    Vector<String> labels = new Vector<String>();
    labels.addElement("AAA");
    labels.addElement("AAC");
    labels.addElement("AAG");
    labels.addElement("AAT");
    labels.addElement("ACA");
    labels.addElement("ACC");
    labels.addElement("ACG");
    labels.addElement("ACT");
    labels.addElement("AGA");
    labels.addElement("AGC");
    labels.addElement("AGG");
    labels.addElement("AGT");
    labels.addElement("ATA");
    labels.addElement("ATC");
    labels.addElement("ATG");
    labels.addElement("ATT");
    labels.addElement("CAA");
    labels.addElement("CAC");
    labels.addElement("CAG");
    labels.addElement("CAT");
    labels.addElement("CCA");
    labels.addElement("CCC");
    labels.addElement("CCG");
    labels.addElement("CCT");
    labels.addElement("CGA");
    labels.addElement("CGC");
    labels.addElement("CGG");
    labels.addElement("CGT");
    labels.addElement("CTA");
    labels.addElement("CTC");
    labels.addElement("CTG");
    labels.addElement("CTT");
    labels.addElement("GAA");
    labels.addElement("GAC");
    labels.addElement("GAG");
    labels.addElement("GAT");
    labels.addElement("GCA");
    labels.addElement("GCC");
    labels.addElement("GCG");
    labels.addElement("GCT");
    labels.addElement("GGA");
    labels.addElement("GGC");
    labels.addElement("GGG");
    labels.addElement("GGT");
    labels.addElement("GTA");
    labels.addElement("GTC");
    labels.addElement("GTG");
    labels.addElement("GTT");
    labels.addElement("TAA");
    labels.addElement("TAC");
    labels.addElement("TAG");
    labels.addElement("TAT");
    labels.addElement("TCA");
    labels.addElement("TCC");
    labels.addElement("TCG");
    labels.addElement("TCT");
    labels.addElement("TGA");
    labels.addElement("TGC");
    labels.addElement("TGG");
    labels.addElement("TGT");
    labels.addElement("TTA");
    labels.addElement("TTC");
    labels.addElement("TTG");
    labels.addElement("TTT");
    
    return labels;

  }
  private static Vector<String> getSpliceLabels() {
    	Vector<String> labels = new Vector<String>();
	labels.addElement("AA");
	labels.addElement("AC");
	labels.addElement("AG");
	labels.addElement("AT");
	labels.addElement("CA");
	labels.addElement("CC");
	labels.addElement("CG");
	labels.addElement("CT");
	labels.addElement("GA");
	labels.addElement("GC");
	labels.addElement("GG");
	labels.addElement("GT");
	labels.addElement("TA");
	labels.addElement("TC");
	labels.addElement("TG");
	labels.addElement("TT");

	return labels;
  }

  public static void main(String[] args) {
    try {
	File file = new File(args[0]);

	BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
	DataInputStream dataIn = new DataInputStream(bis);

	Vector<String> labels = MatrixDisplay.getCodonLabels();

	double dist = 0.01;
	String legend = String.valueOf(dist);
	//Vector d = MatrixDisplay.readMatrix(dataIn);
	Vector<Vector<Double>> d;

	if (args[2].equals("Codon")) {
	  d = MatrixDisplay.readCodonMatrix(dataIn);
	} else {
	  d = MatrixDisplay.readSpliceMatrix(dataIn);
	  labels = MatrixDisplay.getSpliceLabels();
	}

	//Vector d2 = MatrixDisplay.readMatrix(dataIn);

	MatrixDisplay md = new MatrixDisplay(d,labels,labels);

	if (args[3].equals("true")) {
	  md.isRelative = true;
	}
	//md.setSecondMatrix(d2);

	md.setLegend(legend);

	JFrame f = new JFrame();

	f.getContentPane().add(md);

	f.setSize(500,500);
	f.show();

	Thread.sleep(Integer.parseInt(args[1]));

	while ((d = MatrixDisplay.readMatrix(dataIn)) != null  && d.size() > 0) {
	dist += 0.01;
	legend = String.valueOf(dist);
	md.setLegend(legend);
		md.setMatrix(d);
	//d2 = MatrixDisplay.readMatrix(dataIn);
	//md.setSecondMatrix(d2);
	
	Thread.sleep(Integer.parseInt(args[1]));
	}

	} catch (InterruptedException e) {
	    System.out.println("Exception e " + e);
	} catch (IOException e) {
	    System.out.println("Exception e " + e);
	}

    }
}
