package pogvue.gui.hub;

import pogvue.io.AlignFile;
import pogvue.io.FastaFile;
import pogvue.io.FileParse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import pogvue.io.*;
import pogvue.gui.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

import pogvue.gui.menus.*;
import pogvue.gui.renderer.GraphRenderer;
import pogvue.datamodel.*;

/**
 * Created by IntelliJ IDEA. User: mclamp Date: Aug 11, 2007 Time: 4:55:46 PM To
 * change this template use File | Settings | File Templates.
 */
public class GenomeInfoFactory {
  // So this is a train wreck - where do I start?
  public static AlignSplitPanel makePanel(Alignment al, String title,
      double width1, double width2, int offset, int start, int end, int width) {

    AlignSplitPanel asp = new AlignSplitPanel(al, title, width1, width2);

    AlignmentPanel ap1 = asp.getAlignmentPanel1();
    AlignmentPanel ap2 = asp.getAlignmentPanel2();

    AlignViewport av1 = ap1.getAlignViewport();
    AlignViewport av2 = ap2.getAlignViewport();

    av1.setOffset(offset);
    av2.setOffset(offset);

    int startres = (int) ((end - start) / 2 - (width) / av1.getCharWidth());

    av1.setStartRes(startres);
    startres = (int) ((end - start) / 2 - (width) / av2.getCharWidth());

    av2.setStartRes(startres);

    return asp;

  }
  // Ditto
  public static JPanel makeSinglePanel(Alignment al, String title, double width1) {
    JPanel jp = new JPanel(new BorderLayout());

    AlignViewport av = new AlignViewport(al);
    Controller controller = new Controller();

    av.setController(controller);

    AlignmentPanel ap = new AlignmentPanel(av, controller);

    av.setRenderer(new GraphRenderer());
    av.setFont(new Font("Helvetica", Font.PLAIN, 0));
    av.setCharWidth(width1, "AlignmentPanel");
    // av.setPIDBaseline(70);
    av.setCharHeight(10);

    // MenuManager m = new MenuManager(jp, av, controller);

    ap.setMinimumSize(new Dimension(0, 0));

    // This is needed when the panel is brought up from a Choosepanel button.
    ap.repaint();

    jp.add("Center", ap);
    return jp;
  }


}
