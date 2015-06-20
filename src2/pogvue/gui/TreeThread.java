package pogvue.gui;

import pogvue.datamodel.tree.NJTree;
import pogvue.datamodel.Sequence;

import java.awt.*;
import java.util.Vector;

public class TreeThread extends Thread {
  private Sequence[] s;
  private String type;
  private String pwtype;
  private Controller c;
  private AlignViewport av;
  private int start;
  private int end;


  public TreeThread(Controller c,AlignViewport av,Vector vect, String type, String pwtype,int start,int end) {
      this.c = c;
      this.av = av;

      s = new Sequence[vect.size()];

      for (int i=0; i < vect.size(); i++) {
	s[i] = (Sequence)vect.elementAt(i);
      }
      
      init(s,type,pwtype);
      this.start = start;
      this.end   = end;

  }

  public TreeThread(Sequence[] s, String type, String pwtype) {
    init(s,type,pwtype);
  }
  private void init(Sequence[] s, String type, String pwtype) {
    this.s = s;
    this.type = type;
    this.pwtype = pwtype;
  }

  public void run() {
      NJTree tree = new NJTree(s,type,pwtype,start,end);

      System.out.println("Top node " + tree.getTopNode());
      System.out.println("Height "   + tree.getMaxHeight());
      System.out.println("Dist "     + tree.getMaxDist());

      TreePanel treePanel = new TreePanel(null,av,c,tree);
      TreeFrame treeFrame = new TreeFrame(null,treePanel);

      av.setCurrentTree(tree);

      treePanel.setParent(treeFrame);
      treeFrame.setLayout(new BorderLayout());
      treeFrame.add("Center",treePanel);

      treeFrame.setTitle("Jalview Tree");
      treeFrame.setSize(500,500);

      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

      treeFrame.setLocation((screenSize.width - treeFrame.getSize().width) / 2,
		  (screenSize.height - treeFrame.getSize().height) / 2);

      treeFrame.show();

  }
}



