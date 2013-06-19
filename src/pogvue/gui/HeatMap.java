package pogvue.gui;

import pogvue.io.FileParse;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.StringTokenizer;

public final class HeatMap extends JPanel {

    private final int[][] score;
    private final String[] labels;
    private final int[] order;
    private final int   scale = 8;

    private int max = 0;
    private int min = 100000000;

    private double logmax = 0.0;
    private double logmin = 10000000.0;

    private HeatMap(int[][] score,int[] order, String[] labels) {
	this.score = score;
	this.order = order;
	this.labels = labels;

	
	for (int i = 0;i < order.length; i++) {
	    for (int j = 0;j < order.length; j++) {
		int s = score[i][j];

		if (s > max) {max = s;}
		if (s < min) {min = s;}

		if (s/1000.0 > 0) {
		    if (logmax < Math.log(s/1000.0)/Math.log(2)) {logmax = Math.log(s/1000.0)/Math.log(2);}
		
		    if (logmin > Math.log(s/1000.0)/Math.log(2)) {logmin = Math.log(s/1000.0)/Math.log(2);}
		}

	    }
	}
    }

    public void paint(Graphics g) {
	g.setColor(Color.white);
	g.fillRect(0,0,size().width,size().height);

	System.out.println("Max/min " + logmax + " " + logmin);

	for (int i = 0;i < order.length; i++) {

	    for (int j = 0;j < order.length; j++) {

		int ii = i;//order[i]-1;
		int jj = j;//order[j]-1;

		int s2 = score[ii][jj];

		double s;

		if (s2/1000.0 < 1e-7) {
		    s = logmin;
		} else {
		    s = (Math.log(s2/1000.0)/Math.log(2));
		}

		
		if (s > 0 ) {
		    Color c = new Color((int)(255.0*s/(logmax)),0,0);

		    g.setColor(c);
		} else  {
		    Color c = new Color(0,(int)(255.0*s/logmin),(int)(255.0*s/(logmin)));

		    g.setColor(c);
		}
		g.fillRect(jj*scale,ii*scale,scale,scale);

	    }
	}
    }

    public static void main(String[] args) {
	try {
	    int num = Integer.parseInt(args[0]);

	    FileParse sfile = new FileParse(args[1],"File");
	    FileParse ofile = new FileParse(args[2],"File");
	    
	    int[][] score = new int[num][num];
	    
	    int[] order = new int[num];
	    
	    String[] labels = new String[num];
	    
	    String line;
	    int    count = 0;
	    
	    while ((line = sfile.nextLine()) != null) {
		if (count == 0) {
		    StringTokenizer str = new StringTokenizer(line,"\t");
		    int n = 0;
		    while (str.hasMoreTokens()) {

			labels[n] = str.nextToken();

			n++;
		    }
		} else {
		    StringTokenizer str = new StringTokenizer(line,"\t");
		    
		    int n = 0;
		    
		    str.nextToken();
		    while (str.hasMoreTokens()) {
			
			int m = Integer.parseInt(str.nextToken());
			
			score[count-1][n] = m;
			n++;
		    }
		}
		count++;
	    }
	    
	    count = 0;

	while ((line = ofile.nextLine()) != null) {
	    StringTokenizer str = new StringTokenizer(line);

	    while (str.hasMoreTokens()) {
		order[count] = Integer.parseInt(str.nextToken());
		count++;
	    }
	}

	HeatMap hm = new HeatMap(score,order,labels);

	JFrame jf = new JFrame();

	jf.getContentPane().add(hm);

	jf.setSize(700,700);
	jf.show();

	} catch (IOException e) {
	    System.out.println("IO " + e);
	}
    }

}
