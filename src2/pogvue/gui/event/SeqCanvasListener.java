package pogvue.gui.event;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.beans.*;
import java.text.*;

import pogvue.analysis.*;
import pogvue.gui.*;
import pogvue.datamodel.*;
import pogvue.gui.event.*;
import pogvue.gui.renderer.ConsensusRenderer;
import pogvue.gui.renderer.CpGRenderer;
import pogvue.gui.renderer.FrameMismatchRenderer;
import pogvue.gui.renderer.GraphRenderer;
import pogvue.gui.renderer.ConflateAlignRenderer;
import pogvue.gui.schemes.*;
import pogvue.io.*;

public class SeqCanvasListener implements MouseListener, 
			       KeyListener, 
			       MouseMotionListener
			       {

	public SeqCanvas     sc;
	public Controller    controller;
	public AlignViewport av;

	public int mousepos;     // the base directly under the mouse
	
	public int lastres;
	public int lastseq;
	
	public boolean shiftPressed = false;
  
  public SeqCanvasListener(SeqCanvas sc,AlignViewport av,Controller controller) {

    this.sc = sc;
    this.av = av;

    this.controller = controller;
  }

  public void mouseClicked (MouseEvent evt) {

  	int x = evt.getX();
    int y = evt.getY();
    
    av.setXPosition(x);
    av.setYPosition(y);
    
    int newres = av.getNewRes();
    int newseq = av.getNewSeq();

    if (SwingUtilities.isMiddleMouseButton(evt)) {
    	av.setPositions(x,y);

    	newres = av.getNewRes();
    	newseq = av.getNewSeq();
    	
    	controller.handleAlignViewportEvent(new AlignViewportEvent(this,av,AlignViewportEvent.RESHAPE));
    	
    }
  }
  
  public void mouseEntered(MouseEvent evt) {  }
  public void mouseExited(MouseEvent evt) { }
  public void mouseMoved(MouseEvent evt) { 
    evt.getComponent().requestFocus();

    int y = evt.getY();
    int x = evt.getX();

    av.setPositions(x,y);
    
    mousepos = y;

    av.setMousePos(x);
    
    int newres = av.getNewRes();
    int newseq = av.getNewSeq();
    
    // This is updating the status - does this need to be here?
    
    Sequence seq = av.getAlignment().getSequenceAt(newseq);

    if (seq == null) {
      return;
    }
    
    NumberFormat nf = NumberFormat.getInstance();
    
    String formattedNumber = nf.format(av.getOffset() + newres);

    controller.handleStatusEvent(new StatusEvent(this,seq.getName(),StatusEvent.FNAME));
    controller.handleStatusEvent(new StatusEvent(this,formattedNumber,StatusEvent.POSITION));

    // Move out of here
    if (seq instanceof GFF) {
    	// Is this a GFF - find the feature.
    	GFF gff = (GFF)seq;
    	
    	Vector feat = gff.overlaps(newres,newres);
    	for (int i = 0; i < feat.size(); i++) {
    		SequenceFeature sf = (SequenceFeature)feat.elementAt(i);
    		if (sf.getHitFeature() != null) {
		  System.out.println("hit feature " + sf.getHitFeature().getId());
    			controller.handleStatusEvent(new StatusEvent(this,sf.getHitFeature().getId(),StatusEvent.FNAME));
    			controller.handleStatusEvent(new StatusEvent(this,String.valueOf(sf.getScore()),StatusEvent.FSCORE));
    		} else {
    			controller.handleStatusEvent(new StatusEvent(this,sf.getId(),StatusEvent.FNAME));
    			controller.handleStatusEvent(new StatusEvent(this,String.valueOf(sf.getScore()),StatusEvent.FSCORE));
    		}
    	}
    	
    	controller.handleStatusEvent(new StatusEvent(this,formattedNumber,StatusEvent.POSITION));

    }

  }
  
  public void mouseDragged(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();

    av.setPositions(x,y);  
    
    int newres = av.getNewRes();
    int newseq = av.getNewSeq();


    if (shiftPressed) {     // This is for zoom panel
    	av.setMouseRes(newres);
    	av.setMouseSeq(newseq);
    }
    
    av.setMousePos(x);

    if (newres != lastres) {
    	int startres = av.getStartRes() - newres + lastres;
    	int endres   = av.getEndRes()   - newres + lastres;
	
    	if (shiftPressed == true) {
    		
    		controller.handleAlignViewportEvent(new AlignViewportEvent(this,av,AlignViewportEvent.GLASS));
    		
    	} else {
    		if (startres > 0) {
    			
    			av.setStartRes(startres);
    			av.setEndRes(endres);
    			
    			controller.handleAlignViewportEvent(new AlignViewportEvent(this,av,AlignViewportEvent.HSCROLL));
		
    		}
    	}
    }
  }

  public void mousePressed(MouseEvent evt) {

    av.setPositions(evt.getX(),evt.getY());
        
    av.setMouseRes(av.getNewRes());
    av.setMouseSeq(av.getEndRes());
    
    if (shiftPressed) {
    	controller.handleAlignViewportEvent(new AlignViewportEvent(this,av,AlignViewportEvent.GLASS));	
    } else {
    	lastres = av.getNewRes();
    	lastseq = av.getNewSeq();

    	av.setMouseRes(-1);
    	av.setMouseSeq(-1);
    }
  }

  public void mouseReleased(MouseEvent evt) {
      av.setMouseRes(-1);
      av.setMouseSeq(-1);
  }
  public void keyPressed(KeyEvent e) {
  }
  public void keyTyped(KeyEvent e) { 


    boolean redraw = false;

  	char c = e.getKeyChar();

  	if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
  		shiftPressed = true;
  	} else {
  		shiftPressed = false;
  	}
  	
  	// I'm dealing with data here in the view whereas I should be dealing with it in the model (or at least the drawable model)
  	// Let's make a list of operations :
  	//  1. Delete track
  	//  2. Increase track height
  	//  3. Decrease track height
  	//  4. Set hieght by score
  	//  5. Set char width to 3
  	//  6. Set char width to 1
  	//  7. Set char width to .5
  	//  8. Set char width to .01
  	//  9. Set char width to .001
    // 10. Set to large text size
    // 11. Set to char width of 7
    // 12. Set to frame mismatch renderer
    // 13. Set to graph renderer
    // 14. Set to consensus renderer
    // 15. set CpG renderer
    // 16. Hide track
    // 17. Show all tracks
    // 18.  Collapse all tracks
    // 19.  Decrease char width
    // 20.  Increase char width
    // 21.  Show all sequences
    // 22.  Show human sequence
    // 23.  Hide sequences
    // 24.  Hide gappy sequences
    // 25.  Go left
    // 26.  Go right
    // 27.  Go to the start
    // 28.  Go to the end
    // 29.  Recenter
    // 30.  Change zoom level
    // 31.  Move start

  	
  	if        (c == 'd') { av.decreaseTrackHeight();                              redraw = true;
	  //} else if (c == 't') { av.showTrackSelectionFrame();                           redraw = true;
  	} else if (c == 'w') { av.writeGFFConfig();                                            redraw = true;
	} else if (c == 'i') { av.increaseCharHeight();                                   redraw = true;
	} else if (c == 'k') { av.setGFFHeightByScore(!av.getGFFHeightByScore());       redraw = true;
	} else if (c == '`') { av.setCharWidth(3,    "Setting charwidth to 3");           redraw = true;
	} else if (c == '1') { av.setCharWidth(1,    "Setting charwidth to 1");           redraw = true;
	} else if (c == '2') { av.setCharWidth(0.5,  "Setting charwidth to 0.5");           redraw = true;
	} else if (c == '3') { av.setCharWidth(0.01, "Setting charwidth to 0.01");        redraw = true;
	} else if (c == '4') { av.setCharWidth(0.001,"Setting charwidth to .001");       redraw = true;
	} else if (c == 't') { av.setLargeText();                                         redraw = true;
	} else if (c == 'f') { av.setMediumText();                                        redraw = true;
	} else if (c == 'n') { av.setRenderer(new FrameMismatchRenderer());                redraw = true;
	} else if (c == 'r') { av.setRenderer(new GraphRenderer());                          redraw = true;
	} else if (c == 'R') { av.setRenderer(new ConsensusRenderer());                      redraw = true;
	} else if (c == 'g') { av.setRenderer(new CpGRenderer());                            redraw = true;
	} else if (c == 'l') { av.setRenderer(new ConflateAlignRenderer());                    redraw = true;
	} else if (c == 'c') { av.hideTrack(av.getIndex(mousepos));                          redraw = true;
	} else if (c == 'C') { av.showAllTracks();                                            redraw = true;
	} else if (c == 'o') { av.collapseAllTracks();                                         redraw = true;
	} else if (c == '-') { av.zoomOut();                                                 redraw = true;
	} else if (c == '=') { av.zoomIn();                                                 redraw = true;
	} else if (c == 'S') { av.setHuman(!av.getHuman());                                  redraw = true;
	} else if (c == 'x') { av.hideGappySequences();                                           redraw = true;
	} else if (c == 's') { av.toggleSequence();                                           redraw = true;
	} else if (c == 'X') { av.expandRegion();                                          redraw = true;
	  //} else if (c == 'y') { av.toggleTree();                                             redraw = true;
	  //} else if (c == 'Y') { av.findMutations();                                            redraw = true;
	  //} else if (c == 'O') { av.scanLogos();                                                redraw = true;
	  //} else if (c == 'H') { av.showLogoLabels(!av.showLogoLabels());                         redraw = true;
	} else if (c == 'q') { redraw = true;
	} else if (c == 'X') { 
	  // RegionFetchThread rft = new RegionFetchThread(sr.getChr(),sr.getStart(),sr.getEnd(),newfeat);
	  //rft.setActionListener(this);
	  //rft.start();
	}
  	
  	if         (e.getKeyCode() == KeyEvent.VK_LEFT)                     { av.moveLeft();             redraw = true;
  	} else if  (e.getKeyCode() == KeyEvent.VK_RIGHT)                    { av.moveRight();              redraw = true;  
  	} else if  (e.getKeyCode() == KeyEvent.VK_LEFT  && e.isShiftDown()) { av.moveToStart();            redraw = true;
  	} else if  (e.getKeyCode() == KeyEvent.VK_RIGHT && e.isShiftDown()) { av.moveToEnd();               redraw = true;
  	} else if  (e.getKeyCode() == KeyEvent.VK_UP)                       { av.setCharWidth(7.0,"Setting charwidth to 7");   redraw = true;
  	} else if (e.getKeyCode() == KeyEvent.VK_DOWN)                      { av.setFullSpan(sc.getWidth());   redraw = true;
  	}
  	
	if (redraw == true) {
	  controller.handleAlignViewportEvent(new AlignViewportEvent(this,av,AlignViewportEvent.RESHAPE));
	}
  }
  
  public void keyReleased(KeyEvent e) {
    boolean redraw = false;


    if         (e.getKeyCode() == KeyEvent.VK_LEFT)                     { av.moveLeft();    redraw = true;
    } else if  (e.getKeyCode() == KeyEvent.VK_RIGHT)                    { av.moveRight();    redraw = true;
    } else if  (e.getKeyCode() == KeyEvent.VK_LEFT  && e.isShiftDown()) { av.moveToStart();   redraw = true;
    } else if  (e.getKeyCode() == KeyEvent.VK_RIGHT && e.isShiftDown()) { av.moveToEnd();   redraw = true;
    } else if  (e.getKeyCode() == KeyEvent.VK_UP)                       { av.setCharWidth(7.0,"Setting charwidth to 7");   redraw = true;
    } else if (e.getKeyCode() == KeyEvent.VK_DOWN)                      { av.setFullSpan(sc.getWidth());   redraw = true;
    }
  	
      char c = e.getKeyChar();

      if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
        shiftPressed = false;
        av.setMouseRes(-1);
        av.setMouseSeq(-1);
	redraw = true;
      }

      if (redraw == true) {
	controller.handleAlignViewportEvent(new AlignViewportEvent(this,av,AlignViewportEvent.GLASS));
      }	
  }
			       }
