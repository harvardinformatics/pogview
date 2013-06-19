package pogvue.gui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

public final class MatrixCompare extends JPanel {

    private Vector matrix;
    private Vector matrix2;

    private final Vector xlabels;
    private final Vector ylabels;

    private String legend;

    private MatrixCompare(Vector matrix,Vector xlabels, Vector ylabels) {
	this.matrix  = matrix;

	this.xlabels = xlabels;
	this.ylabels = ylabels;
    }

    private void setLegend(String legend) {
	this.legend = legend;
    }

    public void setMatrix(Vector d) {
	this.matrix = d;

	repaint();
    }

    private void setSecondMatrix(Vector d) {
	this.matrix2 = d;

	repaint();
    }

    public void paint(Graphics g) {
	Graphics2D screen2D = (Graphics2D)g;

        screen2D.setColor(Color.white);
        screen2D.fillRect(0, 0, getSize().width, getSize().height);

        screen2D.setColor(Color.red);

	double min = -1;
	double max = -1;

	for (int i = 0; i < matrix.size(); i++) {

	    Vector v = (Vector)matrix.elementAt(i);

	    for (int j = 0; j < v.size(); j++) {

		double d = (Double) v.elementAt(j);
		double droot = d;//Math.sqrt(d);

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
		    double droot = d;//Math.sqrt(d);
		    
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

	    Vector v = (Vector)matrix.elementAt(i);
	    Vector v2 = (Vector)matrix2.elementAt(i);
	    
	    for (int j = 0; j < v.size(); j++) {

		double d = (Double) v.elementAt(j);
		double d2 = (Double) v2.elementAt(j);

		//d = Math.sqrt(d);
		int radius = (int)(d * wspacing/(2*max));

		int x = (j+1)*wspacing;
		int y = (i+1)*hspacing;
		
		//Ellipse2D.Float circle = new Ellipse2D.Float(x, y, radius, radius);
		//screen2D.fill(circle);

		g.setColor(Color.red);
		g.fillRect(x+wspacing/4,y-(int)(d*hspacing/max),wspacing/4,(int)(d*hspacing/max));
		g.setColor(Color.blue);
		g.fillRect(x+wspacing/2,y-(int)(d2*hspacing/max),wspacing/4,(int)(d2*hspacing/max));

		//g.setColor(Color.red);

	//	g.fillRect(x,wspacing/3,
	//		   (i+1)*hspacing -10,
	//		   10);
		//(int)(d*hspacing/(max)));

	//	g.setColor(Color.blue);

		//g.fillRect(x+wspacing/3,wspacing/3,
		//(int)((i+1)*hspacing - d2*hspacing/(max)),
		//(int)(d2*hspacing/(max)));

	    }
	}

	screen2D.setColor(Color.black);

	for (int i = 0; i < xlabels.size(); i++) {
	    String label = (String)xlabels.elementAt(i);

	    screen2D.drawString(label,(i+1)*wspacing,20);

	}

	for (int i = 0; i < ylabels.size(); i++) {
	    String label = (String)ylabels.elementAt(i);

	    screen2D.drawString(label,5,(i+1)*hspacing);

	}
	screen2D.drawString(legend.substring(0,4),20,getSize().height - 10);

    }

    public static Vector readMatrix(DataInputStream din) {
	String line;

	Vector top = new Vector();
	try {
	    while ((line = din.readLine()) != null) {
		if (line.equals("")) {
		    return top;
		}

		StringTokenizer str = new StringTokenizer(line);
		Vector tmp = new Vector();
	        double total = 0;
	
		top.addElement(tmp);
		
		while (str.hasMoreTokens()) {
		    double val = Double.parseDouble(str.nextToken());
                    total += val;
		    tmp.addElement(val);
		}
                for (int i = 0; i < tmp.size(); i++) {
                    double tmpel = (Double) tmp.elementAt(i) /total;
                    tmp.setElementAt(tmpel,i);
                }
               
	    }
	} catch (IOException e) {
	    System.out.println("Exception e " + e);
	}
	return top;
    }

    public static void main(String[] args) {
	try {
	File file = new File(args[0]);

	BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
	DataInputStream dataIn = new DataInputStream(bis);


	Vector labels = new Vector();
        StringTokenizer str = new StringTokenizer(args[1]);

        while (str.hasMoreTokens()) {
          labels.addElement(str.nextToken());
        }
	
	double dist = 0.01;
	String legend = String.valueOf(dist);
	Vector d = MatrixDisplay.readMatrix(dataIn);
	Vector d2 = MatrixDisplay.readMatrix(dataIn);

	MatrixCompare md = new MatrixCompare(d,labels,labels);
	md.setSecondMatrix(d2);

	md.setLegend(legend);

	JFrame f = new JFrame();

	f.getContentPane().add(md);

	f.setSize(500,500);
	f.show();

	Thread.sleep(Integer.parseInt(args[1]));

	//	while ((d = MatrixDisplay.readMatrix(dataIn)) != null  && d.size() > 0) {
	//dist += 0.01;
	//legend = String.valueOf(dist);
	//md.setLegend(legend);
	//	md.setMatrix(d);
	//d2 = MatrixDisplay.readMatrix(dataIn);
	//md.setSecondMatrix(d2);
	
	//Thread.sleep(Integer.parseInt(args[1]));
	//}

	} catch (InterruptedException e) {
	    System.out.println("Exception e " + e);
	} catch (IOException e) {
	    System.out.println("Exception e " + e);
	}

    }
}
