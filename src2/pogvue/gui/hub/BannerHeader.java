package pogvue.gui.hub;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mclamp
 * Date: Aug 8, 2007
 * Time: 3:57:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class BannerHeader extends JPanel {
	String heading;
	int    height = 50;
        Font   font;
       
	public BannerHeader(String heading) {
		this.heading = heading;
		this.font = new Font("Helvetica",Font.ITALIC,40);                

        }

	public void paint(Graphics g) {
          g.setFont(font);
          
          // Fill the background color
          g.setColor(Color.white);
          g.fillRect(0,0,getSize().width,getSize().height);

          // Draw the header
          g.setColor(Color.darkGray);
          g.drawString(heading,10,35);
          g.setColor(Color.black);
          g.drawLine(0,height-1,getSize().width,height-1);
		
	}

	// Needed to tell the layout manager what size to draw it
	public Dimension getPreferredSize() {
		return new Dimension(10,height);
	}
}
