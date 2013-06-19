package pogvue.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class ProgressPanel extends JPanel implements ActionListener {

  private JButton      cancelButton;
  private JProgressBar progressBar;
  private JTextArea    taskOutput;

  private Vector       listeners;

  private int          start;
  private int          end;

  public ProgressPanel(int start, int end, ActionListener l) {
    this.start = start;
    this.end = end;

    addListener(l);

    buildGUI();
  }

  public void setIndeterminate() {
    progressBar.setIndeterminate(true);
  }

  public void setValue(int value) {
    progressBar.setValue(value);
  }

  public void addListener(ActionListener l) {
    if (listeners == null) {
      listeners = new Vector();
    }

    if (!listeners.contains(l)) {
      listeners.addElement(l);

      if (cancelButton != null) {
        cancelButton.addActionListener(l);
      }
    }
  }

  public void buildGUI() {

    cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand("cancel");

    progressBar = new JProgressBar(start, end);
    progressBar.setValue(start);
    progressBar.setStringPainted(true);

    taskOutput = new JTextArea(5, 20);
    taskOutput.setMargin(new Insets(5, 5, 5, 5));
    taskOutput.setEditable(false);

    add(progressBar);
    add(cancelButton);

    for (int i = 0; i < listeners.size(); i++) {
      ActionListener l = (ActionListener) listeners.elementAt(i);

      cancelButton.addActionListener(l);
    }
  }

  public void actionPerformed(ActionEvent e) {

    int value = Integer.parseInt(e.getActionCommand());

    if (value >= 100) {
      progressBar.setValue(100);
      progressBar.setIndeterminate(true);
    } else {
      progressBar.setValue(value);
    }
  }

}