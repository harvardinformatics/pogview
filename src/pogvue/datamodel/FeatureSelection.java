/*
 * FeatureSelection.java
 *
 * Created on December 30, 2007, 10:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package pogvue.datamodel;

//import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import pogvue.gui.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import pogvue.gui.hub.GFFTableModel;

/*
 * @author Michele
 */

public class FeatureSelection implements ListSelectionListener, MouseListener {
  Vector feat;

  KaryotypePanel kp;
  JTable         table;

  /** Creates a new instance of FeatureSelection */
  public FeatureSelection(KaryotypePanel kp, JTable table) {
    feat = new Vector();

    this.kp    = kp;
    this.table = table;
  }
  public void addFeature(SequenceFeature f) {

    if (!feat.contains(f)) {
      feat.add(f);
    }
  }
  public void addFeatures(Vector f) {
    for (int i=0; i < f.size(); i++) {
      addFeature((SequenceFeature)f.elementAt(i));
    }

    System.out.println("ADded " + f.size() + " features");

    kp.repaint();

    table.invalidate();
    table.validate();
    table.revalidate();
    table.paintImmediately(0,0,table.getWidth(),table.getHeight());

  }
  public void deleteFeature(SequenceFeature f) {
    if (feat.contains(f)) {
      feat.remove(f);
    }
  }
  public Vector getFeatures() {
    return feat;
  }
  public void mouseClicked(MouseEvent e) {
    JTable target = (JTable)e.getSource();
    int row = target.getSelectedRow();
    int col = target.getSelectedColumn();

    GFFTableModel tm = (GFFTableModel)table.getModel();

    Object val  = tm.getValueAt(row,col);

    System.out.println("Found " + val);
    if (e.getClickCount() == 2) {
      System.out.println("Double click");


      System.out.println("Selected value " + val + " " + row + " " + col + " " + tm);

      Vector feat = tm.getAllFeatures();
      Vector sel  = new Vector();

      //System.out.println("Feat size " + feat.size());

      removeAllFeatures();

      for (int i = 0; i < feat.size(); i ++) {

        if (tm.getValueAt(i,col).equals(val)) {
          SequenceFeature sf = (SequenceFeature)feat.elementAt(i);

          sel.addElement(sf);
        }
      }

      addFeatures(sel);
    } else if (SwingUtilities.isRightMouseButton(e) && col == 7){
      //try {
      //Desktop.getDesktop().browse(new URI("http://pfam.sanger.ac.uk/family?id="+val));
      //} catch (java.net.URISyntaxException use) {
      //     System.out.println("URI Syntax exception " + use);
      //} catch (java.io.IOException ioe) {
      //    System.out.println("IOException " + ioe);
      // }
    }

  }

  public void mouseEntered (MouseEvent e) {}

  public void mouseExited  (MouseEvent e) {}
  public void mousePressed (MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}
  public void removeAllFeatures() {
    feat = new Vector();
  }
  public void valueChanged(ListSelectionEvent e) {
    ListSelectionModel lm = (ListSelectionModel)e.getSource();

    // There's something odd here - the selection is coming through twice - maybe need the isAdjusting flag'
    int[] rows = table.getSelectedRows();

    GFFTableModel tm = (GFFTableModel)table.getModel();

    Vector feat = tm.getFeatures();

    for (int i = 0; i < rows.length; i++) {
      int j = rows[i];
      System.out.println("Got feature " + ((SequenceFeature)feat.get(j)).getScore());
    }

    System.out.println("Features " + tm.getFeatures().size() + " " + tm);
    System.out.println("Selection changed");
  }

}
