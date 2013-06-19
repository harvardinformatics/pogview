package pogvue.gui.menus;

import pogvue.gui.AlignViewport;
import pogvue.gui.Controller;
import pogvue.gui.event.AlignViewportEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

public final class FontMenu extends PanelMenu implements ActionListener {

    private ButtonGroup faceGroup;
    private ButtonGroup fontGroup;
    private ButtonGroup sizeGroup;


    private JRadioButtonMenuItem  small;
    private JRadioButtonMenuItem  medium;
    private JRadioButtonMenuItem  verysmall;
    private JRadioButtonMenuItem  extremelysmall;
    private JRadioButtonMenuItem  supersmall;
    
    private JRadioButtonMenuItem helv;
    private JRadioButtonMenuItem cour;
    private JRadioButtonMenuItem times;

    private static final int []   sizes = {1,2,4,6,8,10,12,14,16,20,24};
  
    private JRadioButtonMenuItem[] fontButtons;
    
    private static final Hashtable styleHash = new Hashtable();
    static {
	styleHash.put("Plain", Font.PLAIN);
	styleHash.put("Bold", Font.BOLD);
    }
    
    public FontMenu(JPanel panel, AlignViewport av,Controller c) {
	super("Font",panel,av,c);
    }
    
    protected void init() {
	
	helv  = new JRadioButtonMenuItem("Helvetica");
	cour  = new JRadioButtonMenuItem("Courier");
    times = new JRadioButtonMenuItem("Times-Roman");

    faceGroup = new ButtonGroup();

    faceGroup.add(helv);
    faceGroup.add(cour);
    faceGroup.add(times);

    helv.addActionListener(this);
    cour.addActionListener(this);
    times.addActionListener(this);

    add(helv);
    add(cour);
    add(times);

    addSeparator();

    fontGroup = new ButtonGroup();

    int curSize = av.getFont().getSize();
    
    fontButtons = new JRadioButtonMenuItem[sizes.length];

    for (int i=0; i < sizes.length; i++) {
	fontButtons[i] = new JRadioButtonMenuItem(String.valueOf(sizes[i]));
	fontGroup.add(fontButtons[i]);
	fontButtons[i].addActionListener(this);
	add(fontButtons[i]);
    }

    addSeparator();

    sizeGroup = new ButtonGroup();

    medium          = new JRadioButtonMenuItem("Medium view");
    small           = new JRadioButtonMenuItem("Small view");
    verysmall       = new JRadioButtonMenuItem("Very small view");
    extremelysmall  = new JRadioButtonMenuItem("Extremely small view");
    supersmall      = new JRadioButtonMenuItem("Super small view");

    sizeGroup.add(medium);
    sizeGroup.add(small);
    sizeGroup.add(verysmall);
    sizeGroup.add(extremelysmall);
    sizeGroup.add(supersmall);

    medium.addActionListener(this);
    small.addActionListener(this);
    verysmall.addActionListener(this);
    extremelysmall.addActionListener(this);
    supersmall.addActionListener(this);

    add(medium);
    add(small);
    add(verysmall);
    add(extremelysmall);
    add(supersmall);


    if (av.getFont().getName().equals("Helvetica")) {
	helv.setSelected(true);
    } else if (av.getFont().getName().equals("Courier")) {
	cour.setSelected(true);
    } else if (av.getFont().getName().equals("Times-Roman")) {
	times.setSelected(true);
    }

    if (av.getCharWidth() >= 1) {
	for (int i = 0; i < sizes.length; i++) {
	    if (av.getCharWidth() == sizes[i]) {
		fontButtons[i].setSelected(true);
	    }
	}
    } else {
	if (av.getCharWidth() == 0.5) {
	    medium.setSelected(true);
	} else if (av.getCharWidth() == 0.1) {
	    small.setSelected(true);
	} else if (av.getCharWidth() == 0.05) {
	    verysmall.setSelected(true);
	} else if (av.getCharWidth() == 0.01) {
	    extremelysmall.setSelected(true);
	} else if (av.getCharWidth() == 0.001) {
	    supersmall.setSelected(true);
	}
    }
  }
    public void actionPerformed(ActionEvent e) {
	//System.out.println("Action " + e);

	if (e.getSource() == helv) {
	    if (! av.getFont().getName().equals("Helvetica")) {
		Font f   = new Font("Helvetica",
				    av.getFont().getStyle(),
				    av.getFont().getSize());
	    
		av.setFont(f);
		controller.handleAlignViewportEvent(new AlignViewportEvent(this,av,AlignViewportEvent.COLOURING));
	    }
	} else if (e.getSource() == cour) {
	    if (! av.getFont().getName().equals("Courier")) {
		Font f   = new Font("Courier",
				    av.getFont().getStyle(),
				    av.getFont().getSize());
	    
		av.setFont(f);
		controller.handleAlignViewportEvent(new AlignViewportEvent(this,av,AlignViewportEvent.COLOURING));
	    }
	} else if (e.getSource() == times) {
	    if (! av.getFont().getName().equals("Times-Roman")) {
		Font f   = new Font("Times-Roman",
				    av.getFont().getStyle(),
				    av.getFont().getSize());
		
		av.setFont(f);
		controller.handleAlignViewportEvent(new AlignViewportEvent(this,av,AlignViewportEvent.COLOURING));
	    }

	} else if (isFontButton(e.getSource()) != 0) {
	    int size = isFontButton(e.getSource());
	    if (av.getFont().getSize() != size) {
		Font f   = new Font(av.getFont().getName(),
				    av.getFont().getStyle(),
				    size);
		
		av.setFont(f);
		controller.handleAlignViewportEvent(new AlignViewportEvent(this,av,AlignViewportEvent.COLOURING));
	    }
	} else  if (e.getSource() == small) {
	    av.setCharWidth(0.1,"FontMenu");

	    av.setFont(new Font(av.getFont().getName(),av.getFont().getStyle(),0));
	    controller.handleAlignViewportEvent(new AlignViewportEvent(this,av,AlignViewportEvent.COLOURING));
	}else if (e.getSource() == verysmall) {
	    av.setCharWidth(0.05,"FontMenu");
	    av.setFont(new Font(av.getFont().getName(),av.getFont().getStyle(),0));
	    controller.handleAlignViewportEvent(new AlignViewportEvent(this,av,AlignViewportEvent.COLOURING));
	}else if (e.getSource() == extremelysmall) {
	    av.setCharWidth(0.01,"FontMenu");
	    av.setFont(new Font(av.getFont().getName(),av.getFont().getStyle(),0));
	    controller.handleAlignViewportEvent(new AlignViewportEvent(this,av,AlignViewportEvent.COLOURING));
	}else if (e.getSource() == supersmall) {
	    av.setCharWidth(0.001,"FontMenu");
	    av.setFont(new Font(av.getFont().getName(),av.getFont().getStyle(),0));
	    controller.handleAlignViewportEvent(new AlignViewportEvent(this,av,AlignViewportEvent.COLOURING));
	}else if (e.getSource() == medium) {
	    av.setCharWidth(0.5,"FontMenu");
	    av.setFont(new Font(av.getFont().getName(),av.getFont().getStyle(),0));
	    controller.handleAlignViewportEvent(new AlignViewportEvent(this,av,AlignViewportEvent.COLOURING));
	}

    }
   private int isFontButton(Object source) {
	//System.out.println("Font button " + source);

	for (int i = 0; i < sizes.length; i++) {
	    // System.out.println("Button " + fontButtons[i]);
	    if (source == fontButtons[i]) {
		//System.out.println("Foudn size " + sizes[i]);

		return sizes[i];
	    }
	}
	return 0;
    }



}


