package pogvue.gui;

import pogvue.datamodel.tree.Tree;
import pogvue.datamodel.Sequence;
import pogvue.datamodel.tree.BinaryNode;
import pogvue.datamodel.tree.BinaryNode;
import pogvue.gui.event.*;
import pogvue.util.Format;

import java.awt.*;
import java.awt.event.*;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class TreeCanvas extends Panel implements MouseListener,
					    MouseMotionListener,
					    RubberbandListener,
					    SequenceSelectionListener,
                                            KeyListener {


  private final Color grayRed      = new Color(237,171,173);
  private final Color grayBlue     = new Color(123,145,166);
  private final Color grayYellow   = new Color(255,250,165);
  private final Color grayGreen    = new Color(115,177,138);
            
  private final Color darkRed      = Color.red.darker();
  private final Color darkBlue     = Color.blue.darker();
  private final Color darkYellow   = Color.yellow.darker();
  private final Color darkGreen    = Color.green.darker();
            
  private final Color duskyRed     = new Color(255,84,90);
  private final Color duskyGreen   = new Color(93,252,128);
  private final Color duskyBlue    = new Color(93,128,255);
  private final Color duskyYellow  = new Color(255,250,155);
  



  public int mousex;
  public int mousey;

  private Tree tree;
  private final Object parent;

  private Font font;
  private Font distFont;
  private int  fontSize = 12;
  private Font smallFont = new Font("Helvetica",Font.PLAIN,10);
  boolean showDistances = false;
  private boolean showBootstrap = false;

  private final int offx = 20;
  private final int offy = 20;

  private int threshold;

  RubberbandRectangle rubberband;

  private Selection selected;
  private Vector    listeners;

  private Hashtable nameHash = new Hashtable();
  private Hashtable nodeHash = new Hashtable();

  public TreeCanvas(Object parent, Tree tree, Selection selected) {
    this.tree     = tree;
    this.parent   = parent;
    this.selected = selected;

    treeInit();
  }
  public void setSelected(Selection selected) {
    this.selected = selected;
  }

  private void treeInit() {
      addMouseListener(this);
      addMouseMotionListener(this);
      addKeyListener(this);

      tree.findHeight(tree.getTopNode());

      setBackground(Color.white);

  }

  public void setTree(Tree tree) {
    this.tree = tree;
    tree.findHeight(tree.getTopNode());
  }

  private void  drawProb(BinaryNode node, int xend, int ypos,Graphics g) {
    if (node.getMaxProb() != null) {
      if (node.getProb()[0] == 0.25) {
	g.setColor(Color.lightGray);
      } else {
	int site = tree.getSite();

	int i = 0;
	int xpos = size().width - 11*12;

	while (i < 10) {
	  int c  = node.getMaxChar(site+i);
	  
	  if (c == 0) {
	    
	    if (node.isLeaf()) {
	      g.setColor(darkGreen);
	      g.fillRect(xpos,ypos-6,12,12);

	      g.setColor(Color.black);
	      g.drawString("A",xpos,ypos+6);
	    } else {
	      g.setColor(darkGreen);
	      //g.drawString("A",xend+2 + (i*14),ypos+7);
	    }

	  } else if (c == 1) {
	    if (node.isLeaf()) {
	      g.setColor(darkBlue);
	      g.fillRect(xpos,ypos-6,12,12);

	      g.setColor(Color.white);
	      g.drawString("C",xpos,ypos+6);

	    } else {
	      g.setColor(darkBlue);
	      //g.drawString("C",xend+2 + (i*14),ypos+7);
	    }


	  } else if (c == 2) {
	    if (node.isLeaf()) {
	      g.setColor(darkYellow);
	      g.fillRect(xpos,ypos-6,12,12);

	      g.setColor(Color.black);
	      g.drawString("G",xpos,ypos+6);
	    } else {
	      g.setColor(darkYellow);
	      //g.drawString("G",xend+2+ (i*14),ypos+7);	      
	    }


	  } else if (c == 3) {
	    if (node.isLeaf()) {
	      g.setColor(darkRed);
	      g.fillRect(xpos,ypos-6,12,12);
	      g.setColor(Color.white);
	      //g.drawString("T",xend+2 + (i*14),ypos+5);
	      g.drawString("T",xpos,ypos+6);
	    } else {
	      g.setColor(darkRed);
	      //g.drawString("T",xend+2 + (i*14),ypos+5);	      
	    }

	  } else if (c == -1) {
	    g.setColor(Color.gray);
	  }
	  xpos += 12;
	  i++;	
	}
      }
    }
  }
  private void drawNode(Graphics g,BinaryNode node, float chunk, float scale, int width,int offx, int offy) {
    if (node == null) {
      return;
    }
    
    if (node.left() == null && node.right() == null) {
      // Drawing leaf node
      
      float height = node.height;
      float dist   = node.dist;
      
      int xstart = (int)((height-dist)*scale) + offx;
      int xend   =   (int)(height*scale)      + offx;
      
      int ypos = (int)(node.ycount * chunk) + offy;
      
      g.setColor(Color.black);

      if (node.getProb() != null && node.getProb()[0] == 0.25) {
	g.setColor(Color.lightGray);
      }
      
      // Draw horizontal line
      g.drawLine(xstart,ypos,xend,ypos);
      
      // Is there a mutation on this branch?

      // This code should go into the BinaryNode class
      if (node.getParent() != null && node.getMaxChar() != node.getParent().getMaxChar()) {
	//System.out.println("Mutation here");
	g.setColor(Color.pink);
	g.fillRect((xstart+xend)/2 - 5,ypos-5,10,10);
      } 
      System.out.println("Mutationcount " + node.mutationCount);
      if (node.mutationCount > 0) {
	g.setColor(Color.pink);
	g.fillRect((xstart+xend)/2 - 5,ypos-5,10,10);

	g.setColor(Color.black);
	int val = (int)node.mutationCount;
	g.setFont(smallFont);
	g.drawString(String.valueOf(val),(xstart+xend)/2, ypos);
	g.fillRect((xstart+xend)/2,ypos - (int)(10*val/100),5,(int)(10*val/100));
      }
      
      String nodeLabel = "";
      
      if (showDistances && node.dist > 0) {
        nodeLabel = new Format("%5.2f").form(node.dist);
      }
      
      if (showBootstrap) {
        if (showDistances) {
          nodeLabel = nodeLabel + " : ";
        }
        nodeLabel = nodeLabel + String.valueOf(node.getBootstrap());
      }
      
      if (! nodeLabel.equals("")) {
	g.setFont(distFont);
	g.setColor(Color.black);
	if (node.getProb() != null && node.getProb()[0] == 0.25) {
	  g.setColor(Color.lightGray);
	}
	g.drawString(nodeLabel,xstart+3,ypos - 2);
	g.setFont(font);
      }

      // Colour selected leaves differently
      String name    = node.getName();
      FontMetrics fm = g.getFontMetrics(font);
      int charWidth  = fm.stringWidth(node.getName()) + 3;
      int charHeight = fm.getHeight();

      Rectangle rect = new Rectangle(xend+20,ypos-charHeight,
				     charWidth,charHeight);

      nameHash.put(node.element(),rect);
      nodeHash.put(node,rect);

      if (selected.contains((Sequence)node.element())) {
        g.setColor(Color.gray);

        g.fillRect(xend + 20, ypos - charHeight + 3,charWidth,charHeight);
        g.setColor(Color.white);
      }
      if (node.getProb() != null && node.getProb()[0] == 0.25) {
	g.setColor(Color.lightGray);
      }
      g.drawString(node.getName(),xend+20,ypos);
      g.setColor(Color.black);

      drawProb(node,xend,ypos,g);
      g.setColor(Color.black);

    } else {

      drawNode(g,(BinaryNode)node.left(), chunk,scale,width,offx,offy);
      drawNode(g,(BinaryNode)node.right(),chunk,scale,width,offx,offy);

      float height = node.height;
      float dist   = node.dist;

      int xstart = (int)((height-dist)*scale) + offx;
      int xend   = (int)(height       *scale) + offx;
      int ypos   = (int)(node.ycount  *chunk) + offy;

      g.setColor(node.color.darker());

      // Draw horizontal line
      g.drawLine(xstart,ypos,xend,ypos);

      if (node.getParent() != null && node.getMaxChar() != node.getParent().getMaxChar()) {
	//System.out.println("Mutation here");
	g.setColor(Color.pink);
	g.fillRect((xstart+xend)/2 - 5,ypos-5,10,10);
      }

      if (node.mutationCount > 0) {
	g.setColor(Color.pink);
	g.fillRect((xstart+xend)/2 - 5,ypos-5,10,10);

	g.setColor(Color.black);
	int val = (int)node.mutationCount;
	g.setFont(smallFont);
	g.drawString(String.valueOf(val),(xstart+xend)/2, ypos);
	g.fillRect((xstart+xend)/2,ypos - (int)(10*val/100),5,(int)(10*val/100));

      }

      int ystart = offy + 10;
      int yend   = offy + 10;
      try {
	  ystart = (int)(((BinaryNode)node.left()) .ycount * chunk) + offy;
	  yend   = (int)(((BinaryNode)node.right()).ycount * chunk) + offy;
      } catch (Exception e) {
	  e.printStackTrace();
      }

      Rectangle pos = new Rectangle(xend-2,ypos-2,5,5);
      nodeHash.put(node,pos);
      g.drawLine((int)(height*scale) + offx, ystart,
                 (int)(height*scale) + offx, yend);
      // Draw the probs

      drawProb(node,xend+2,ypos,g);

      if (showDistances && node.dist > 0 && scale*node.dist > 20 && (xend-xstart+1)*scale > 20) {
        g.setFont(distFont);
        g.drawString(new Format("%5.2f").form(node.dist),xstart+3,ypos - 2);
        g.setFont(font);

      }

    }
  }
  private Object findElement(int x, int y) {
    Enumeration keys = nameHash.keys();

    while (keys.hasMoreElements()) {
      Object ob = keys.nextElement();
      Rectangle rect = (Rectangle)nameHash.get(ob);

      if (x >= rect.x && x <= (rect.x + rect.width) &&
	  y >= rect.y && y <= (rect.y + rect.height)) {
	return ob;
      }
    }

    keys = nodeHash.keys();

    while (keys.hasMoreElements()) {
      Object    ob   = keys.nextElement();
      Rectangle rect = (Rectangle)nodeHash.get(ob);
      
      if (x >= rect.x && x <= (rect.x + rect.width) &&
	  y >= rect.y && y <= (rect.y + rect.height)) {
	return ob;
      }
    }
    return null;

  }

  public BinaryNode findNode(int x, int y) {
    Enumeration keys = nodeHash.keys();

    while (keys.hasMoreElements()) {
      Object    ob   = keys.nextElement();
      Rectangle rect = (Rectangle)nodeHash.get(ob);
      
      if (x >= rect.x && x <= (rect.x + rect.width) &&
	  y >= rect.y && y <= (rect.y + rect.height)) {
	return (BinaryNode)ob;
      }
    }
    return null;

  }

  private void pickNodes(Rectangle pickBox, Selection sel) {
    int width  = getSize().width;
    int height = getSize().height;
    
    BinaryNode top = tree.getTopNode();
    
    float wscale = (float)(width*.8-offx*2)/tree.getMaxHeight();

    if (top.count == 0) {
      top.count = ((BinaryNode)top.left()).count + ((BinaryNode)top.right()).count ;
    }
    
    float chunk = (float)(height-offy*2)/top.count;

    pickNode(pickBox,sel,top,chunk,wscale,width,offx,offy);
  }

  private void pickNode(Rectangle pickBox, Selection sel, BinaryNode node, float chunk, float scale, int width,int offx, int offy) {
    if (node == null) {
      return;
    }

    if (node.left() == null && node.right() == null) {
      float height = node.height;
      float dist   = node.dist;

      int xstart = (int)((height-dist)*scale) + offx;
      int xend   = (int)(height*scale) + offx;

      int ypos = (int)(node.ycount * chunk) + offy;

      if (pickBox.contains(new Point(xend,ypos))) {
        if (node.element() instanceof Sequence) {
          Sequence seq = (Sequence)node.element();
          if (sel.contains(seq)) {
            sel.removeElement(seq);
          } else {
            sel.addElement(seq);
          }
        }
      }
    } else {
      pickNode(pickBox,sel,(BinaryNode)node.left(), chunk,scale,width,offx,offy);
      pickNode(pickBox,sel,(BinaryNode)node.right(),chunk,scale,width,offx,offy);
    }
  }

  private void setColor(BinaryNode node, Color c) {
    if (node == null) {
      return;
    }

    if (node.left() == null && node.right() == null) {
      node.color = c;

      if (node.element() instanceof Sequence) {
	//((Sequence)node.element()).setColor(c);
      }
    } else {
      node.color = c;
      setColor((BinaryNode)node.left(),c);
      setColor((BinaryNode)node.right(),c);
    }
  }


  public boolean handleSequenceSelectionEvent(SequenceSelectionEvent evt) {
      invalidate();
      validate();

      return true;
  }

  public void paint(Graphics g) {
    if (getSize() != null) {
      draw(g,getSize().width,getSize().height);
      
      if (threshold != 0) {
	g.setColor(Color.black);
	g.drawLine(threshold,0,threshold,getSize().height);
      }
    } else {
      draw(g,500,500);
      
      if (threshold != 0) {
	g.setColor(Color.black);
	g.drawLine(threshold,0,threshold,getSize().height);
      }
    }
  }

  public int getFontSize() {
    return fontSize;
  }

  public void setFontSize(int fontSize) {
    this.fontSize = fontSize;
  }

  private void draw(Graphics g, int width, int height) {

    font      = new Font("Helvetica",Font.PLAIN,fontSize);
    distFont  = new Font("Helvetica",Font.PLAIN,fontSize-2);

    g.setFont(font);

    float wscale = (float)(width*.8-offx*2)/tree.getMaxHeight();

    BinaryNode top = tree.getTopNode();

    if (top.count == 0) {
      top.count = ((BinaryNode)top.left()).count + ((BinaryNode)top.right()).count ;
    }
    float chunk = (float)(height-offy*2)/top.count;
    
    nodeHash = new Hashtable();
    nameHash = new Hashtable();

    drawNode(g,tree.getTopNode(),chunk,wscale,width,offx,offy);
  }

  public void mouseReleased(MouseEvent e) { }
  public void mouseEntered(MouseEvent e) { }
  public void mouseExited(MouseEvent e) { }
  public void mouseClicked(MouseEvent e) {
  }

  public boolean handleRubberbandEvent(RubberbandEvent evt) {
    System.out.println("Rubberband handler called in TreePanel with " +
                       evt.getBounds());

    Rubberband rb = (Rubberband)evt.getSource();

    pickNodes(evt.getBounds(),selected);
    
    return true;
  }
  
  public void mouseDragged(MouseEvent e) {}
  public void mouseMoved(MouseEvent e) {
    mousex = e.getX();
    mousey = e.getY();
  }
  public void mousePressed(MouseEvent e) {
      int x = e.getX();
      int y = e.getY();
      
      //System.out.println("Mouse pressed " + x + " " + y);

      Object ob = findElement(x,y);
      
      //System.out.println("Element " + ob);
      
      if (ob instanceof Sequence) {

	  Sequence s = (Sequence)ob;
	  
	  fireTreeSelectionEvent(new TreeSelectionEvent(this,s));
	  repaint();

      } else if (ob instanceof BinaryNode) {

	//System.out.println("Found node " + ob);
	  BinaryNode tmpnode = (BinaryNode)ob;

	  tree.swapNodes(tmpnode);
	  tree.reCount(tree.getTopNode());
	  tree.findHeight(tree.getTopNode());
	  
	  repaint();
      } else {
          // Find threshold
	  
          if (tree.getMaxHeight() != 0) {
	      float threshold = (float)(x - offx)/(float)(getSize().width*0.8 - 2*offx);
	      //            threshold = x;
	      repaint();
	      //System.out.println("Trehsold " + threshold);
	      //tree.getGroups().removeAllElements();
	     // tree.groupNodes(tree.getTopNode(),threshold);
	      
	      //for (int i=0; i < tree.getGroups().size(); i++) {
		  
	//	  int tmp = i%(7);
	//	  Color col = new Color((int)(Math.random()*255),
	//				(int)(Math.random()*255),
	//				(int)(Math.random()*255));
		  
		  //setColor((BinaryNode)tree.getGroups().elementAt(i),col.brighter());
		  
		  // l is vector of Objects
		  //Vector l = tree.findLeaves((BinaryNode)tree.getGroups().elementAt(i),new Vector());
		  
	     // }
	      fireTreeSelectionEvent(new TreeSelectionEvent(this,null));
	      repaint();
	      
	  }
      }
      //      }
  }
  public void hashNodes(BinaryNode node, Hashtable hash) {
  
    hash.put(node.toString(),node);
    
    if (node.left() != null) {
      hashNodes((BinaryNode)node.left(),hash);
    }
    if (node.right() != null) {
      hashNodes((BinaryNode)node.right(),hash);
    }
  }

  public void keyReleased(KeyEvent evt) {}
  public void keyTyped(KeyEvent evt) {}
  public void keyPressed(KeyEvent evt) {
    //System.out.println("Event " + evt + " " + mousex + " " + mousey);

    char c = evt.getKeyChar();

    if ( c == 'r') {
      Object ob = findElement(mousex,mousey);
      
      if (ob instanceof BinaryNode) {
	Hashtable nodes = new Hashtable();
	
	hashNodes(tree.getTopNode(),nodes);
	
	tree.root((BinaryNode)ob);
	
	Hashtable newnodes = new Hashtable();
	
	hashNodes(tree.getTopNode(),newnodes);
	
	
	Enumeration en = nodes.keys();
	
	while (en.hasMoreElements()) {
	  String str= (String)en.nextElement();
	  //System.out.println("Hash " + nodes.get(str) + " : " + newnodes.get(str));
	}
	tree.findHeight(tree.getTopNode());
	repaint();
      }
      //System.out.println("Object " + ob);
    } else if (c == 'p') {
      printNode((BinaryNode)tree.getTopNode());
      System.out.println(";");
    } else if (c == 'a') {
      BinaryNode ob = findNode(mousex,mousey);
      
      //System.out.println("Object " + ob);
      if (ob instanceof BinaryNode) {
	BinaryNode node1 = (BinaryNode)ob;
	BinaryNode node2 = (BinaryNode)node1.parent;
	  
	tree.addNode(node1);
	repaint();
      }
      
    } else if (c == 'd') {
      BinaryNode node = findNode(mousex,mousey);

      //System.out.println("Object " + node);
      if (node != null) {
	tree.deleteNode(node);
	repaint();
      }
    }
  }
  public void printNode(BinaryNode node) {

    if (node.getName() != null) {

      System.out.print(node.getName() + ":" + node.dist);
    } else {
      System.out.print("(");
      printNode((BinaryNode)node.left());
      System.out.print(",");
      printNode((BinaryNode)node.right());
      System.out.print("):" + node.dist);
    }
  }
      


    public void addTreeSelectionListener(TreeSelectionListener l) {
	if (listeners == null) {
	    listeners = new Vector();
	}
	listeners.addElement(l);
    }
    private void fireTreeSelectionEvent(TreeSelectionEvent e) {
	for (int i =0; i < listeners.size(); i++) {
	    if (listeners.elementAt(i) instanceof TreeSelectionListener) {
	      //System.out.println("Firign tree event");
		((TreeSelectionListener)listeners.elementAt(i)).handleTreeSelectionEvent(e);
	    }
	}
    }
    public void setShowDistances(boolean state) {
	this.showDistances = state;
    }
    public boolean getShowDistances() {
	return showDistances;
    }
    public void setShowBootstrap(boolean state) {
      this.showBootstrap = state;
    }
  public boolean getShowBootstrap() {
    return showBootstrap;
  }
  public Dimension getPreferredSize() {
    return getMinimumSize();
  }
  public Dimension getMinimumSize() {
    return new Dimension(500,500);
  }
}



















