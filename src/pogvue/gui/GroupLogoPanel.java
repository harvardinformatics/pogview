package pogvue.gui;

import org.jibble.epsgraphics.EpsGraphics2D;

import pogvue.util.QuickSort;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import java.awt.font.*;
import java.awt.geom.*;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;
import pogvue.io.*;
import pogvue.gui.*;
import pogvue.gui.hub.*;
import pogvue.gui.event.*;
import pogvue.analysis.*;

public class GroupLogoPanel extends JPanel implements KeyListener {
  boolean useimage = true;
  double scalex = 0.3;

  int width;
  int height;

  int count = 10;

  int charwidth;
  int charheight = 40;

  int w = 7;
  int h = 15;

  Vector pwms;

  Color[]    colors = new Color[4];
  
  boolean sizeByInfo = false;

  Image img;
  boolean showlabels = false;

  String fontstr = "Arial";

  public GroupLogoPanel(Vector pwms) {
    this.pwms = pwms;

    count = pwms.size();

    colors[0] = Color.green.darker();
    colors[1] = Color.red.darker();
    colors[2] = Color.blue;
    colors[3] = Color.yellow.darker();

    setBackground(Color.white);    
    this.setFocusable(true); 

    addKeyListener(this);
  }
  
  public AffineTransform getScaleTransform(float xscale, float yscale) {
    AffineTransform ascale = new AffineTransform();
    
    ascale.scale(xscale,yscale);
    
    return ascale;
  }

  public int drawLetter(Graphics2D g2,String s,Font font,Color c,FontRenderContext frc, int x, int y) {
    
    TextLayout        tstring = new TextLayout(s, font, frc);
    
    g2.setColor(c);
    tstring.draw(g2,x,y);
    
    float  yoff      = (float)tstring.getBounds().getHeight();
    
    return (int)yoff;
  }
  public Font scaleFont(Font font, float xval,float yval) {
    AffineTransform ascale = getScaleTransform(xval,yval);
    
    Font newfont = font.deriveFont(ascale);
    
    return newfont;
  }
  
  public void paintComponent(Graphics g) {
    Graphics2D        g2     = (Graphics2D)g;
    FontRenderContext frc    = g2.getFontRenderContext();    

    if (useimage == true && (img == null || size().width != width || size().height != height)) {
      img = createImage(size().width,size().height);
      
      g2 = (Graphics2D)img.getGraphics();
    }

    g2.setColor(Color.white);
    g2.fillRect(0,0,size().width,size().height);

    width  = size().width;
    height = size().height;

    int ycoord = charheight;



    Font labelfont = new Font(fontstr,Font.PLAIN,charheight-2);
    FontMetrics fm = g2.getFontMetrics(labelfont);

    int maxwidth = 0;

    for (int jj = 0; jj < pwms.size(); jj++) {
      Pwm pwm = (Pwm)pwms.elementAt(jj);

      String name = pwm.getName();
      if (fm.stringWidth(name) > maxwidth) {
	maxwidth = fm.stringWidth(name);
      }
    }


    int xoffset = 0;

    if (showlabels == true) {
      xoffset = maxwidth;
    }

    for (int jj = 0; jj < pwms.size(); jj++) {
      System.out.println("Pwm " + pwms.elementAt(jj));
      Pwm p = (Pwm)pwms.elementAt(jj);

      if (showlabels == true) {
	g2.setFont(labelfont);
	g2.setColor(Color.black);
	g2.drawString(p.getName(),0,ycoord + charheight + 2);

      }

      double[] pwm = p.getPwm();
    
      int len = pwm.length/4;
      
      String s      = "A";
      
      // Create fonts and text layouts
      Font  font      = new Font(fontstr,Font.BOLD,charheight);

      // Scale the font to the right width and height

      TextLayout tstring = new TextLayout("A", font, frc);

      float      theight = (float)tstring.getBounds().getHeight();
      float      twidth  = (float)tstring.getBounds().getWidth();

      float xscale = (float)(25/twidth);
      float yscale = (float)((getSize().height - 10)/theight);
	
      tstring = new TextLayout("A", font, frc);

      theight = (float)tstring.getBounds().getHeight();
      twidth  = (float)tstring.getBounds().getWidth();
      
      w = (int)(twidth*len + 20);
      h = (int)(theight + 10);
      
      for (int i = 0; i < len; i++) {

	int charheight = 0;

	// Get info content for this column  bases are in ATCG order
	int ii  = 0;
	double bit = 0;
	
	while (ii < 4) {
	  double pi = pwm[i*4+ii];
	  
	  if (pi > 0) {
	    bit += pi * Math.log(pi)/Math.log(2);
	    
	  }
	  ii++;
	}
	
	bit = 2+ bit;

	
	ii = 0;

	// find the order of the elements
	float[] vals   = new float[4];
	Integer[] order =  new Integer[4];
	
	while (ii < 4) {
	  vals[ii] = (float)pwm[i*4+ii];
	  order[ii] = ii;
	  ii++;
	}
	
	QuickSort.sort(vals,order);
	
	ii = 0;

	while (ii < 4) {
	  int j = order[ii].intValue();
	  
	  String str = " ";
	  
	  if (j == 0) {
	    str = "A";
	  }
	  if (j == 1) {
	    str = "T";
	  }
	  if (j == 2) {
	    str = "C";
	  }
	  if (j == 3) {
	    str = "G";
	  }

	  float val = (float)(vals[ii]);
	  
	  if (sizeByInfo == true) {
	    val = (float)(val*bit/1.5);
	  }

	  Font charfont    = scaleFont(font,(float)scalex,(float)(val));
	  
	  charheight      += drawLetter(g2,str,charfont,colors[j],frc,(int)(xoffset + 10+i*twidth*scalex),ycoord + h-charheight);
	  
	  ii++;
	}
      }	    
      ycoord += charheight + 5;
    }
    if (useimage == true) {
      g.drawImage(img,0,0,this);
    }
  }
  
  public static TFMatrix stringToMatrix(String str) {
    
    // Matrices go like this

    // position 1    A   T   C   G
    // position 2    A   T   C   G

    // etc.  So for a string we'll have strlen rows,  and a 1 or a 0 in each of the positions

    int i = 0;

    double[][] out = new double[str.length()][4];
    char[]     arr = str.toCharArray();

    while (i < str.length()) {

      int j = 0;

      while (j < 4) {
	out[i][j] = 0;
	j++;
      }
      if (arr[i] == 'A') {
	out[i][0] = 1;
      } else if (arr[i] == 'T') {
	out[i][1] = 1;
      } else if (arr[i] == 'C') {
	out[i][2] = 1;
      } else if (arr[i] == 'G') {
	out[i][3] = 1;
      }
      i++;
    }

    TFMatrix strmat = new TFMatrix(out,str.length(),4);

    strmat.setName(str);

    return strmat;
  }

  public void setSizeByInfo(boolean sizeByInfo) {
    this.sizeByInfo = sizeByInfo;
  }
  public Dimension getMinimumSize() {
    return new Dimension(w,count*(charheight+10));
  }
  public Dimension getPreferredSize() {
    return new Dimension(w,count*(charheight+10));
  }

  public void keyPressed(KeyEvent e) {
  }
  public void keyTyped(KeyEvent e) { 

    char c = e.getKeyChar();
    System.out.println("Key typed " + c);

    if (c == 'i') {
      charheight++;
      img = null;
      repaint();
    } else if (c == '-') {
      charheight--;
      img = null;
      repaint();
    } else if (c == 'X') {
      scalex = scalex * 1.1;
      repaint();
    } else if (c == 'x') {
      scalex = scalex * 0.9;
      repaint();
    } else if (c == 'l') {
      showlabels = !showlabels;
      img = null;
      repaint();
    } else if (c == 'p') {
      JFileChooser fc = new JFileChooser();
      
      int returnVal = fc.showSaveDialog(this);
      
      File file = fc.getSelectedFile();
      
      if (file != null) {
      	jibbleprint(file);
      }
    }
      
  }
  public void keyReleased(KeyEvent e) {
  }

  private void jibbleprint(File file) {
    try {

      useimage = false;
      FileOutputStream outputStream = new FileOutputStream(file);
      
      EpsGraphics2D g = new EpsGraphics2D("demo",outputStream,0,0,size().width,size().height);

      System.out.println("Painting postscript");
      paint(g);
      System.out.println("Done");
	
      g.flush();
      g.close();

      useimage = true;
    } catch (Exception e2) {
      e2.printStackTrace();
    }
    
  }

  public static void main(String[] args) {
    try {

      PwmLineFile pwmfile = new PwmLineFile(args[0],"File");
      
      pwmfile.parse();
      
      Vector pwms = pwmfile.getPwmMatrices();
      
      AlignViewport av = new AlignViewport();
      
      JFrame jf = new JFrame("Logo test");
      JPanel   p = new JPanel();
      JScrollPane jsp = new JScrollPane(p);
      

      jf.getContentPane().setLayout(new BorderLayout());
      jf.getContentPane().add("Center",jsp);

      jsp.setBackground(Color.white);      
      p.setBackground(Color.white);
      p.setLayout(new GridLayout(pwms.size(),1));

      GroupLogoPanel lp = new GroupLogoPanel(pwms);
      lp.setSizeByInfo(true);
      lp.setBackground(Color.white);
      lp.requestFocus();

      p.add(lp);

      Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
		    
      jf.setLocation(sd.width / 2 - 700 / 2,
		     sd.height / 2 - 500 / 2);
		    
      jf.setTitle("LogoPanel - " + args[0] + " pwms " + pwms.size() + " logos");
      jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      jf.setSize(1200,1000);
	  
      jf.setVisible(true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

	

