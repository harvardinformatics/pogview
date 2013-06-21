package pogvue.gui.renderer;

import pogvue.gui.*;
import pogvue.datamodel.*;

import java.awt.*;
import java.util.*;


public interface RendererI {
	
  public void drawSequence(Graphics2D g, Sequence seq,
  		int start, int end, 
  		int x1,    int y1, 
  		double width, int height,
  		boolean showScores, boolean displayBoxes, boolean displayText, 
  		Vector pid, 
  		int seqnum,
  		AlignViewport av, 
  		Hashtable props,
  		int intpid[][]);
  
  
}