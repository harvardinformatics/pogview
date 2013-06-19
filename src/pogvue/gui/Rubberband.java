// Adapted from code in "Graphic JAVA volume 1".
package pogvue.gui;

import pogvue.gui.event.RubberbandEvent;
import pogvue.gui.event.RubberbandListener;
import pogvue.util.ListenList;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;


/**
 * Draws and controls the rubberband used for selections.
 */
abstract public class Rubberband extends ListenList implements MouseListener, 
                                                               MouseMotionListener {
   private final Point anchorPt    = new Point(0,0);
   private final Point stretchedPt = new Point(0,0);
   private final Point lastPt      = new Point(0,0);
   private final Point endPt       = new Point(0,0);

   private Component component;
   private boolean   firstStretch = true;
   private boolean   active = false;
   private int       modifiers;

   protected abstract void drawLast(Graphics g);
   protected abstract void drawNext(Graphics g);

   public Rubberband() {
   }
   public Rubberband(Component c) {
      listeners = new Vector();
      setComponent(c);
   }
   public void setActive(boolean b) {
      active = b;
   }
   private boolean isRbButton(MouseEvent e) {
       return (e.getModifiers() & InputEvent.BUTTON2_MASK) != 0;
      }
  
   public Component getComponent() {
      return this.component;
   }

   private void setComponent(Component c) {
      component = c; 

      component.addMouseListener(this);
      component.addMouseMotionListener(this);
   }

   private void    setModifiers(int modifiers) {
      this.modifiers = modifiers;
   }
   public void mousePressed(MouseEvent event) {   
      if(isActive() && isRbButton(event)) {
         anchor(event.getPoint());
      }
   }
   public void mouseClicked(MouseEvent event) {
      if(isActive() && isRbButton(event)) {
         end(event.getPoint());
      }
   }
   public void mouseReleased(MouseEvent event) {
      if(isActive() && isRbButton(event)) {
         setModifiers(event.getModifiers());
         end(event.getPoint());
      }
   }
   public void mouseDragged(MouseEvent event) {
      if(isActive() && isRbButton(event)) {
         stretch(event.getPoint());
      }
   }
   public void mouseEntered(MouseEvent event) {
   }
   public void mouseExited(MouseEvent event) {
   }
   public void mouseMoved(MouseEvent event) {
   }

   private boolean isActive    () { return active;      }
   public Point   getAnchor   () { return anchorPt;    }
   public int     getModifiers() { return modifiers;   }
   public Point   getStretched() { return stretchedPt; }
   public Point   getLast     () { return lastPt;      }
   public Point   getEnd      () { return endPt;       }

   private void anchor(Point p) {
      firstStretch = true;
      anchorPt.x = p.x;
      anchorPt.y = p.y;

      stretchedPt.x = lastPt.x = anchorPt.x;
      stretchedPt.y = lastPt.y = anchorPt.y;
   }
   private void stretch(Point p) {
      lastPt.x      = stretchedPt.x;
      lastPt.y      = stretchedPt.y;
      stretchedPt.x = p.x;
      stretchedPt.y = p.y;

      Graphics g = component.getGraphics();
      if(g != null) {
         try {
            g.setXORMode(component.getBackground());

            if(firstStretch) firstStretch = false;
            else                     drawLast(g);

            drawNext(g);
         }
         finally {
            g.dispose();
         }
      }
   }
   private void end(Point p) {
      lastPt.x = endPt.x = p.x;
      lastPt.y = endPt.y = p.y;

      Graphics g = component.getGraphics();
      if(g != null) {
         try {
            g.setXORMode(component.getBackground());
            drawLast(g);
         }
         finally {
            g.dispose();
            if (!firstStretch) {
               fireRubberbandEvent(new RubberbandEvent(this,getBounds()));
            }
         }
      }
   }
   Rectangle getBounds() {
      return new Rectangle(stretchedPt.x < anchorPt.x ? 
                           stretchedPt.x : anchorPt.x,
                           stretchedPt.y < anchorPt.y ? 
                           stretchedPt.y : anchorPt.y,
                           Math.abs(stretchedPt.x - anchorPt.x),
                           Math.abs(stretchedPt.y - anchorPt.y));
   }

   Rectangle lastBounds() {
      return new Rectangle(lastPt.x < anchorPt.x ? lastPt.x : anchorPt.x,
                           lastPt.y < anchorPt.y ? lastPt.y : anchorPt.y,
                           Math.abs(lastPt.x - anchorPt.x),
                           Math.abs(lastPt.y - anchorPt.y));
   }

   private void fireRubberbandEvent(RubberbandEvent evt) {
      for (int i=0;i<listeners.size();i++) {
         RubberbandListener l = (RubberbandListener)listeners.elementAt(i);
         l.handleRubberbandEvent(evt);
      }
   }
}
