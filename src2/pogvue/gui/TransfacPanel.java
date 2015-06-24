package pogvue.gui;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;
import pogvue.io.*;
import pogvue.gui.*;
import pogvue.gui.hub.*;
import pogvue.gui.event.*;
import pogvue.analysis.*;

public class TransfacPanel extends JPanel {

    private LogoPanel logo;
    private JLabel    label;

    private TFMatrix  matrix;

    public TransfacPanel(TFMatrix matrix) {
	this.matrix     = matrix;
	
	// 3 columns
	FormLayout layout = new FormLayout("pref, pref:grow",  // 2 Columns
					   "pref:grow");                 // 1 Row
      
	setLayout(layout);
      
	CellConstraints cc = new CellConstraints();

	label = new JLabel(matrix.getName());
	logo  = new LogoPanel(matrix);

	add(label,        cc.xy(1,1, "fill, fill"));   // Col 1 , Row 1
	add(logo,         cc.xy(2,1, "fill, fill"));   // Col 2 , Row 1

    }

    public static void main(String[] args) {
	try {
	    String tffile = args[0];
	    
	    TFMatrixFile tfm = new TFMatrixFile(tffile,"File");

	    Vector matrices = tfm.getMatrices();

	    JFrame jf = new JFrame("Test logo");
	    JPanel jp = new JPanel();

	    JScrollPane jsp = new JScrollPane(jp);

	    jp.setLayout(new GridLayout(matrices.size(),1));

	    for (int i = 0;i < matrices.size(); i++) {
		
		TransfacPanel tfp = new TransfacPanel((TFMatrix)matrices.elementAt(i));

		jp.add(tfp);
	    }

	    jf.getContentPane().setLayout(new BorderLayout());
	    jf.getContentPane().add(jsp);
	    jf.setSize(500,1000);
	    jf.setVisible(true);

	} catch (IOException e) {
	    System.out.println("Exception "+ e);
	}
    }
}
