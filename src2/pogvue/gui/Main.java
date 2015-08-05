/*
 * Main.java
 *
 * Created on August 14, 2007, 2:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pogvue.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import pogvue.datamodel.Alignment;
import pogvue.datamodel.Sequence;
import pogvue.gui.menus.MenuManager;
import pogvue.gui.renderer.ConsensusRenderer;
import pogvue.gui.renderer.GraphRenderer;
import pogvue.io.GappedFastaFile;

/**
 *
 * @author mclamp
 */
public class Main implements ActionListener {
  JFrame bf;
  JPanel bp;
  JButton b;
  
  /** Creates a new instance of Main */
  public Main() {
    initComponents();
  }
  
  public void initComponents() {
    bf = new JFrame();
    bp = new JPanel();
    b  = new JButton("Click me!");
   
    bp.setSize(500,500);
    bp.setLayout(new BorderLayout());
    bp.add("Center",b);
    bf.getContentPane().add(bp);
    
    b.addActionListener(this);
    bf.setSize(500,500);
    bf.setVisible(true);
    bf.repaint();

    System.out.println("Panel size is " + bp.getSize());
  }
  
  public void actionPerformed(ActionEvent e) {
    JFrame jf = createAlignmentPanel();
  }
  public static void main(String[] args) {

    Main m = new Main();
    
    
  }
  
//  public static JFrame createAlignFrame() {
//    try {
//      
//      String chr = "1";
//      int    chrstart = 1;
//      int    chrend   = 10000;
//      
//             
//      String fasta_url = AlignViewport.getFastaURL() + "?query=" + chr + "&start=" + chrstart + "&end=" + chrend; 
//      String gff_url   = AlignViewport.getGFFURL()   + "?query=" + chr + "&start=" + chrstart + "&end=" + chrend;
//      
//      GappedFastaFile ff = new GappedFastaFile(fasta_url,"URL");
//      Sequence[]     s  = ff.getSeqsAsArray();
//      Alignment       al = new Alignment(s);
//   
//      JFrame jf = new JFrame();
//      
//      AlignFrame af1 = new AlignFrame(null,al);
//      AlignFrame af2 = new AlignFrame(null,al);
//      
//      AlignViewport av2 = af2.getAlignViewport();
//      
//   
//      af2.getViewport().setKmers(af1.getViewport().getKmers());
//      af2.getViewport().setFont(new Font("monospaced",Font.PLAIN,10));
//      af2.getViewport().setRenderer(new ConsensusRenderer());
//      
//      av2.setCharWidth(10,"Minipog in FileMenu");
//      af1.getViewport().setMinipog(af2);
//      
//      JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT,af1,af2);
//      
//      jsp.setOneTouchExpandable(true);
//      jsp.setDividerLocation(500);
//      jf.getContentPane().add(jsp);
//      jf.setSize(1000,1000);
//      
//      af1.getViewport().setRenderer(new GraphRenderer());
//      
//      af1.getViewport().setFont(new Font("Helvetica",Font.PLAIN,0));
//      
//      af1.getViewport().setCharWidth(0.1,"AlignFrame");
//      
//      
//      af1.getViewport().setPIDBaseline(70);
//      af1.getViewport().setCharHeight(10);
//      af2.getViewport().setCharHeight(15);
//  
//      
//      return jf;
//      
//    } catch (IOException e) {
//      System.out.println("Exception " + e);
//    }
//    return null;
//  } 
   
  public static JFrame createAlignmentPanel() {
    try {
      
      String chr = "1";
      int    chrstart = 1;
      int    chrend   = 10000;
      
      String fasta_url = AlignViewport.getFastaURL() + "?query=" + chr + "&start=" + chrstart + "&end=" + chrend; 
      String gff_url   = AlignViewport.getGFFURL()   + "?query=" + chr + "&start=" + chrstart + "&end=" + chrend;
      
      GappedFastaFile ff = new GappedFastaFile(fasta_url,"URL");
      Sequence[]     s  = ff.getSeqsAsArray();
      Alignment       al = new Alignment(s);
   
      JFrame jf = new JFrame();
      
      AlignViewport  av         = new AlignViewport(al);
      Controller     controller = new Controller();
      AlignmentPanel ap         = new AlignmentPanel(av,controller);
        
      av.setRenderer(new GraphRenderer());
      av.setFont(new Font("Helvetica",Font.PLAIN,0));
      av.setCharWidth(0.1,"AlignFrame");
      //av.setPIDBaseline(70);
      av.setCharHeight(12);

      //MenuManager m = new MenuManager(ap, av, controller);  
   
      AlignViewport  av2 = new AlignViewport(al);
     // Controller     c2  = new Controller();
      AlignmentPanel ap2 = new AlignmentPanel(av2,controller);
      
      av2.setFont(new Font("monospaced",Font.PLAIN,10));
      av2.setRenderer(new ConsensusRenderer());
        
      av2.setCharWidth(10,"Minipog in FileMenu");
      av.setMinipog(ap2);
        
      JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT,ap,ap2);
        
      jsp.setOneTouchExpandable(true);
      jsp.setDividerLocation(500);
      
      // These make sure the JSPlitPane can resize the panels.
      ap.setMinimumSize(new Dimension(0,0));
      
      ap2.setMinimumSize(new Dimension(0,0));
      ap2.setDoubleBuffered(true);
      
      jf.getContentPane().add(jsp);
      jf.setSize(1000,1000);
      jf.setVisible(true);
        
      av2.setCharHeight(12);
        
      
      // This is needed when the panel is brought up from a Choosepanel button.
      ap.repaint();
      ap2.repaint();
      
      return jf;
      
    } catch (IOException e) {
      System.out.println("Exception " + e);
    }
    return null;
  } 
}
