package pogvue.gui.hub;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.*;
import java.io.*;
import java.util.concurrent.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA.
 * User: mclamp
 * Date: Aug 10, 2007
 * Time: 4:51:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class GeneSelectPanel extends JPanel implements ActionListener {
    String     genestr;
    
    JTextField gene;
    JTextField padding;
    
    FormLayout layout;

    JProgressBar progressBar;


    public GeneSelectPanel() {
	buildPanel();
    }
    
    public void initComponents(){
	gene     = new JTextField();
	padding  = new JTextField();
    }
    
    public void buildPanel() {
	initComponents();
	
	layout = new FormLayout(
				"right:max(40dlu;pref), 3dlu, max(150dlu;pref), 20dlu",
				"p"
				);
	
	setLayout(layout);
	setBorder(Borders.DIALOG_BORDER);
	
	CellConstraints cc = new CellConstraints();
	
	add(new JLabel("Gene ID"),       cc.xy (1,1));
	add(gene,                        cc.xy (3,1));
	
    }
    
    public String getGeneString() {
	return gene.getText();
    }

    public void createProgressBar() {
	// Change layout to include a progress bar and a label
	
	layout.appendRow(new RowSpec("5dlu"));
	layout.appendRow(new RowSpec("pref"));

	CellConstraints cc = new CellConstraints();
	
	// Insert progress bar

	progressBar = new JProgressBar();

	//progressBar.setIndeterminate(true);
	progressBar.setMinimum(0);
	progressBar.setMaximum(1000);
	progressBar.setValue(0);
	add(progressBar, cc.xy(3,3));

    }	
    public void fetchGenes(String idstr) {
	createProgressBar();

	// Create thread that fetches the genes (???)

	GetGenesByIdThread  t = new GetGenesByIdThread(getGeneString());

	t.setActionListener(this);
	t.start();

	//ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 
	//							1, 
	//							50000L, TimeUnit.SECONDS, 
	//new LinkedBlockingQueue());

    //tpe.execute(t);
    //tpe.shutdown();

	//t.start();

	//	while (tpe.isTerminated() == false) {//t.isAlive()) {
    //   System.out.println("Running");
    //	    try {
    //		Thread.sleep(1000);
    //	    } catch (InterruptedException e) {
    //		System.out.println("Exception " + e);
    //	    }
    //	}
    //	System.out.println("Finished");

    //	StringBuffer genes = t.getOutput();
    //	System.out.println("Output 2 " + genes.toString().length());

	//remove(progressBar);

	//layout.removeRow(3);
	//layout.removeRow(2);

    
    //try {

    //	String line;
    //	BufferedReader r = GenomeInfoFactory.getGenesById(idstr);

    //	while ((line = r.readLine()) != null) {
    //	    genes.append(line + "\n");
    //	    bar.setValue(bar.getValue()+1);
    //	}

    // } catch (IOException e1) {
    //	e1.printStackTrace();	
	//	} catch (InterruptedException e) {
	//e.printStackTrace();
  
    }
public void actionPerformed(ActionEvent e) {
    //System.out.println("Action "  + e);// + e.getActionCommand() + " " + e.getModifiers());

    if (! e.getActionCommand().equals("Line")) {
	progressBar.setMaximum(Integer.parseInt(e.getActionCommand()));
    } else {
	progressBar.setValue(progressBar.getValue()+1);
    }
}

}
