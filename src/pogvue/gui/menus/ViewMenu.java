package pogvue.gui.menus;

import pogvue.datamodel.*;

import pogvue.gui.*;
import pogvue.gui.event.FontChangeEvent;
import pogvue.gui.event.AlignViewportEvent;
import pogvue.gui.renderer.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

public class ViewMenu extends PanelMenu implements ActionListener {
	private ButtonGroup renderGroup;

	private JRadioButtonMenuItem renderer;
	private JRadioButtonMenuItem kmerrenderer;
	private JRadioButtonMenuItem baseRenderer;
	private JRadioButtonMenuItem consBaseRenderer;
	private JRadioButtonMenuItem confAlignRenderer;
	private JRadioButtonMenuItem unconsBaseRenderer;
	private JRadioButtonMenuItem blockRenderer;
	private JRadioButtonMenuItem pidRenderer;
	private JRadioButtonMenuItem tranRenderer;
	private JRadioButtonMenuItem rtranRenderer;
	private JRadioButtonMenuItem frameRenderer;
	private JRadioButtonMenuItem wobbleRenderer;
	private JRadioButtonMenuItem startStopRenderer;
	private JRadioButtonMenuItem kmerFreqRenderer;
	private JRadioButtonMenuItem CpGRenderer;

	private JRadioButtonMenuItem consensusRenderer;
	private JRadioButtonMenuItem increaseHeightButton;
	private JRadioButtonMenuItem decreaseHeightButton;

	public ViewMenu(JPanel panel, AlignViewport av, Controller c) {
		super("View", panel, av, c);
	}

	protected void init() {
		renderGroup = new ButtonGroup();

		renderer           = new JRadioButtonMenuItem("Percent identity plot");
		kmerrenderer       = new JRadioButtonMenuItem("Colour match/mismatch");
		baseRenderer       = new JRadioButtonMenuItem("Colour by base");
		consBaseRenderer   = new JRadioButtonMenuItem("Colour conserved bases");
		confAlignRenderer  = new JRadioButtonMenuItem("Conflate alignment");
		unconsBaseRenderer = new JRadioButtonMenuItem("Colour unconserved bases");
		blockRenderer      = new JRadioButtonMenuItem("Colour by conserved block");
		pidRenderer        = new JRadioButtonMenuItem("Colour by column PID");
		tranRenderer       = new JRadioButtonMenuItem("Show forward 3 frame translation of selected sequence");
		rtranRenderer      = new JRadioButtonMenuItem("Show reverse 3 frame translation of selected sequence");
		frameRenderer      = new JRadioButtonMenuItem("Colour by unconserved frame");
		wobbleRenderer     = new JRadioButtonMenuItem("Colour by frame PID");
		consensusRenderer  = new JRadioButtonMenuItem("Colour non-consensus bases");
		startStopRenderer  = new JRadioButtonMenuItem("Colour start/stop sites");
		kmerFreqRenderer   = new JRadioButtonMenuItem("Colour by kmer frequency");
		CpGRenderer        = new JRadioButtonMenuItem("Colour CpG density");

		increaseHeightButton = new JRadioButtonMenuItem("Increase text height");
		decreaseHeightButton = new JRadioButtonMenuItem("Decrease text height");

		increaseHeightButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
				ActionEvent.CTRL_MASK));
		decreaseHeightButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
				ActionEvent.CTRL_MASK));

		renderGroup.add(renderer);
		renderGroup.add(kmerrenderer);
		renderGroup.add(baseRenderer);
		renderGroup.add(consBaseRenderer);
		renderGroup.add(confAlignRenderer);
		renderGroup.add(unconsBaseRenderer);
		renderGroup.add(blockRenderer);
		renderGroup.add(pidRenderer);
		renderGroup.add(tranRenderer);
		renderGroup.add(rtranRenderer);
		renderGroup.add(frameRenderer);
		renderGroup.add(wobbleRenderer);
		renderGroup.add(increaseHeightButton);
		renderGroup.add(decreaseHeightButton);
		renderGroup.add(consensusRenderer);
		renderGroup.add(startStopRenderer);
		renderGroup.add(kmerFreqRenderer);
		renderGroup.add(CpGRenderer);

		renderer.addActionListener(this);
		kmerrenderer.addActionListener(this);
		baseRenderer.addActionListener(this);
		consBaseRenderer.addActionListener(this);
		confAlignRenderer.addActionListener(this);
		unconsBaseRenderer.addActionListener(this);
		pidRenderer.addActionListener(this);
		tranRenderer.addActionListener(this);
		rtranRenderer.addActionListener(this);
		frameRenderer.addActionListener(this);
		wobbleRenderer.addActionListener(this);
		consensusRenderer.addActionListener(this);
		kmerFreqRenderer.addActionListener(this);
		CpGRenderer.addActionListener(this);

		increaseHeightButton.addActionListener(this);
		decreaseHeightButton.addActionListener(this);

		add(renderer);
		add(kmerrenderer);
		add(baseRenderer);
		add(consBaseRenderer);
		add(confAlignRenderer);
		add(unconsBaseRenderer);
		add(blockRenderer);
		add(pidRenderer);
		add(tranRenderer);
		add(rtranRenderer);
		add(frameRenderer);
		add(wobbleRenderer);
		add(consensusRenderer);
		add(kmerFreqRenderer);
		add(CpGRenderer);

		add(increaseHeightButton);
		add(decreaseHeightButton);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JRadioButtonMenuItem) {

			String name = ((JRadioButtonMenuItem) e.getSource()).getLabel();

			if (name.equals("Percent identity plot")) {
				av.setRenderer(new GraphRenderer());
			} else if (name.equals("Colour by base")) {
				av.setRenderer(new BaseRenderer());
			} else if (name.equals("Colour conserved bases")) {
				av.setRenderer(new ConservedBaseRenderer());
			} else if (name.equals("Conflate alignment")) {
				av.setRenderer(new ConflateAlignRenderer());
			} else if (name.equals("Colour unconserved bases")) {
				av.setRenderer(new UnconservedBaseRenderer());
			} else if (name.equals("Colour by column PID")) {
				av.setRenderer(new PercentIdentityRenderer());
			} else if (name.equals("Colour by unconserved frame")) {
				av.setRenderer(new FrameMismatchRenderer());
			} else if (name.equals("Colour by frame PID")) {
				av.setRenderer(new WobbleExonScoreRenderer());
			} else if (name.equals("Colour non-consensus bases")) {
				av.setRenderer(new ConsensusRenderer());
			} else if (name.equals("Colour start/stop sites")) {
				av.setRenderer(new StartStopRenderer());
			} else if (name.equals("Colour CpG density")) {
				av.setRenderer(new CpGRenderer());
			} else if (name.equals("Increase text height")) {
				av.setCharHeight(av.getCharHeight() + 1);
				controller.handleAlignViewportEvent(new AlignViewportEvent(this, av,
						AlignViewportEvent.RESHAPE));
			} else if (name.equals("Decrease text height")) {
				if (av.getCharHeight() > 1) {
					av.setCharHeight(av.getCharHeight() - 1);
					controller.handleAlignViewportEvent(new AlignViewportEvent(this, av,
							AlignViewportEvent.RESHAPE));
				}
			}

			controller.handleFontChangeEvent(new FontChangeEvent(this, av.getFont()));

		}

	}

}
