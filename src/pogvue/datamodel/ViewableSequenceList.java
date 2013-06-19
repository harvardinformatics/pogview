package pogvue.datamodel;

import pogvue.gui.renderer.*;

public class ViewableSequenceList {
	private SequenceList list;
	
	private double        charWidth;
	private int           charHeight;
	private boolean       scoreByHeight;
	private boolean       showText;
	private TrackRenderer renderer;
	
	private long     viewstart;
	private long     viewend;
	
	// So I'm dealing with data here in the view whereas I should be dealing with it in the model (or at least the drawable model)
	// Let's make a list of operations :
	//  1. Delete track
	//  2. Increase track height
	//  3. Decrease track height
	//  4. Set height by score
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

}
