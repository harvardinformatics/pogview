package pogvue.feature;

import javax.swing.*;
import java.awt.*;

public interface Feature  {
	
	public void draw(Graphics2D g, int xoffset, int yoffset);

	public String toString();

}
