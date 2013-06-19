/* Jalview - a java multiple alignment editor
 * Copyright (C) 1998  Michele Clamp
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package pogvue.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public final class ThresholdPanel extends Panel implements ActionListener,
                                                     AdjustmentListener {

  private final int                low;
  private final int                high;

  private final Scrollbar          sb;
  private final Label              label;
  private final TextField          tf;
  private final GridBagLayout      gb;
  private final GridBagConstraints gbc;

  public ThresholdPanel(JPanel parent,String label,int low, int high, int value) {

    this.low = low;
    this.high = high;

    gb = new GridBagLayout();
    gbc = new GridBagConstraints();

    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 100;
    gbc.weighty = 100;

    setLayout(gb);

    this.sb = new Scrollbar(Scrollbar.HORIZONTAL,value,(low-high)/100,low,high+1);
    this.sb.addAdjustmentListener(this);
    this.label = new Label(label);
    this.tf = new TextField(Integer.toString(value),3);
    this.tf.addActionListener(this);

    add(this.label,gb,gbc,0,0,1,1);
    add(tf,gb,gbc,1,0,1,1);

    gbc.insets = new Insets(10,10,10,10);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    add(sb,gb,gbc,0,1,2,1);
  }

  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == tf) {
      sb.setValue(Integer.valueOf(tf.getText()).intValue());
    }
  }

  public void adjustmentValueChanged(AdjustmentEvent evt) {
    if (evt.getSource() == sb) {
      tf.setText(Integer.toString(sb.getValue()));
    } 
  }

  public Dimension minimumSize() {
    return new Dimension(250,150);
  }

  public Dimension preferredSize() {
    return minimumSize();
  }

  private void add(Component c,GridBagLayout gbl, GridBagConstraints gbc,
                  int x, int y, int w, int h) {

    gbc.gridx = x;
    gbc.gridy = y;
    gbc.gridwidth = w;
    gbc.gridheight = h;

    gbl.setConstraints(c,gbc);

    add(c);
  }

  public void setValue(int value) {
    sb.setValue(value);
    setText(value);
  }

  public String getText() {
    return tf.getText();
  }

  public void setText(int threshold) {
    tf.setText(Integer.toString(threshold));
  }
  public void setText(String threshold) {
    tf.setText(threshold);
  }

  public int getSBValue() {
    return sb.getValue();
  }
}

