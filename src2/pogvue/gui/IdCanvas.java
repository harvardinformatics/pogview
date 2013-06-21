package pogvue.gui;


import pogvue.datamodel.*;

import pogvue.gui.event.AlignViewportEvent;
import pogvue.gui.event.AlignViewportListener;
import pogvue.gui.event.SequenceSelectionEvent;
import pogvue.gui.event.SequenceSelectionListener;

import java.awt.*;

public final class IdCanvas extends ControlledCanvas implements
    AlignViewportListener, ControlledObjectI, SequenceSelectionListener {
  private Image               img         = null;
  private Graphics            gg          = null;

  private int                 imgWidth;
  private int                 imgHeight;

  private final AlignViewport av;

  private boolean             paintFlag   = false;

  private int                 idWidth     = 60;
  private int                 maxIdLength = -1;
  private String              maxIdStr    = null;

  public boolean              buffer      = true;

  public IdCanvas(AlignViewport av, Controller c) {
    this.av = av;

    setController(c);
  }

  public boolean handleAlignViewportEvent(AlignViewportEvent e) {
    if (e.getType() == AlignViewportEvent.HSCROLL) {
      paintFlag = false;
    }
    repaint();
    return true;
  }

  public boolean handleSequenceSelectionEvent(SequenceSelectionEvent e) {
    paint(this.getGraphics());
    return true;
  }

  private void drawIdString(Graphics g, Sequence ds, int i, int starty, int ypos) {
    int charHeight = av.getCharHeight();

    if (av.getSelection().contains(ds)) {
      gg.setColor(Color.gray);
      gg.fillRect(0,
          av.getPixelHeight(starty, i, charHeight) + ypos,
          getSize().width, charHeight);
      gg.setColor(Color.white);
    } else {
      gg.setColor(Color.white);
      gg.fillRect(0,
          av.getPixelHeight(starty, i, charHeight) + ypos,
          getSize().width, charHeight);
      gg.setColor(Color.black);
    }

    String string = ds.getName();
    gg.drawString(string, 0, av.getPixelHeight(starty, i, charHeight)
        + ypos - 5);

  }

  public void paintComponent(Graphics g) {

    Alignment align = av.getAlignment();
    int charWidth = (int) av.getCharWidth();
    int charHeight = av.getCharHeight();
    Font f = av.getIdFont();

    if (av.useImage() == false) {
      gg = g;
      g.setFont(f);
      paintFlag = false;

      imgWidth = size().width;
      idWidth = size().width;

    } else {

      if (img == null || imgWidth != size().width || imgHeight != size().height
          || paintFlag) {

        imgWidth = size().width;
        idWidth = size().width;

        FontMetrics fm = g.getFontMetrics(f);

        idWidth = fm.stringWidth(maxId(fm));
        imgHeight = size().height;

        if (imgWidth <= 0) {
          imgWidth = 700;
        }
        if (imgHeight <= 0) {
          imgHeight = 300;
        }

        img = createImage(imgWidth, imgHeight);

        gg = img.getGraphics();
        gg.setColor(Color.white);
        gg.fillRect(0, 0, imgWidth, imgHeight);

        gg.setFont(f);

        fm = gg.getFontMetrics(f);

        paintFlag = false;

      }
    }

    // Fill in the background
    if (av.useImage() == true) {

      if (gg == null) {
        img = createImage(imgWidth, imgHeight);
        gg = img.getGraphics();
      }
      gg.setColor(Color.white);
      gg.fillRect(0, 0, imgWidth, imgHeight);
    } else {
      g.setColor(Color.white);
      g.fillRect(0, 0, imgWidth, imgHeight);

      gg = g;
    }
    Color currentColor     = Color.white;
    Color currentTextColor = Color.black;

    // Which ids are we printing
    int starty = av.getStartSeq();
    int endy = av.getEndSeq();

    // Now draw the id strings

    int count = 0;

    for (int i = 0; i < align.getHeight(); i++) {
      Sequence s = (Sequence) align.getSequenceAt(i);

      if (!av.hiddenSequences().contains(s) && !(s.getSequence().length() > 0 && charWidth < .1)) {
        count++;

        if (count >= starty && count <= endy) {
          gg.setColor(Color.white);
          gg.fillRect(0, av.getPixelHeight(starty, count, charHeight), size().width,
              charHeight);
        }

        gg.setColor(Color.black);

        String string = s.getDisplayId();

        gg.drawString(string, 0, av.getPixelHeight(starty, count,charHeight) - 3);
      }

    }
    if (av.useImage()) {
      g.drawImage(img, 0, 0, this);
    } else {
      gg = null;
    }
  }

  public Dimension getPreferredSize() {
    if (idWidth != 0) {
      return new Dimension(idWidth + 20, size().height);
    } else {
      return new Dimension(100, 100);
    }

  }

  public int maxIdLength() {
    if (maxIdLength == -1) {
      int max = 0;
      Alignment al = av.getAlignment();

      int i = 0;

      while (i < al.getHeight() && al.getSequenceAt(i) != null) {
        if (al.getSequenceAt(i).getName().length() > max) {
          max = al.getSequenceAt(i).getName().length();
        }
        i++;
      }
      maxIdLength = max;
    }
    return maxIdLength;
  }

  private String maxId(FontMetrics fm) {
    if (maxIdStr == null) {
      Alignment al = av.getAlignment();

      int max = 0;
      String maxStr = "";
      int i = 0;

      while (i < al.getHeight() && al.getSequenceAt(i) != null) {
        Sequence s = al.getSequenceAt(i);

        String str = s.getDisplayId();
        if (fm.stringWidth(str) > max) {
          max = fm.stringWidth(str);
          maxStr = str;
        }
        i++;
      }
      maxIdStr = maxStr;
    }
    return maxIdStr;
  }
}
