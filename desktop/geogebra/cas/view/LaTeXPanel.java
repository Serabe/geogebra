package geogebra.cas.view;

import geogebra.euclidian.DrawEquation;
import geogebra.main.Application;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class LaTeXPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private Application app;	
	private String latex;	
	private BufferedImage image;
	private Graphics2D g2image;
	private Dimension equSize;
	
	public LaTeXPanel(Application app) {
		this.app = app;
		ensureImageSize(100, 100);
	}
	
	public void setLaTeX(String latex) {
		if (latex.equals(this.latex)) return;
		
		this.latex = latex;
		
		// draw equation to get its size
		equSize = drawEquationToImage();		
		
		// check if image was big enough for equation
		if (ensureImageSize(equSize.width, equSize.height)) {
			equSize = drawEquationToImage();
		}
		
		setPreferredSize(equSize);
		setSize(equSize);	
		validate();
	}		
	
	private Dimension drawEquationToImage() {
		g2image.setBackground(getBackground());
		g2image.clearRect(0, 0, image.getWidth(), image.getHeight());
		
		g2image.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
							RenderingHints.VALUE_ANTIALIAS_ON);
		

		geogebra.common.awt.Dimension fd = app.getDrawEquation().
				drawEquation(app, null, new geogebra.awt.Graphics2D(g2image), 0, 0, latex, 
				app.getPlainFontCommon(), false, new geogebra.awt.Color(getForeground()), 
				new geogebra.awt.Color(getBackground()), true);	

		return new Dimension((int)fd.getWidth(), (int)fd.getHeight());
	}
	
	private boolean ensureImageSize(int width, int height) {
		if (image == null || image.getWidth() < width || image.getHeight() < height) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			g2image = image.createGraphics();
			return true;
		}		
		return false;
	}
	
	@Override
	public void paint(Graphics g) {
		if (app.exporting){
			app.getDrawEquation();
			//draw full resolution image directly on g
			app.getDrawEquation().drawEquation(app, null, new geogebra.awt.Graphics2D((Graphics2D) g), 0, 0, latex, app.getPlainFontCommon(), 
					false, new geogebra.awt.Color(getForeground()), new geogebra.awt.Color(getBackground()), true);
		} else {
			// draw part of image that contains equation
			if (image != null && equSize != null) {
				g.drawImage(image, 0, 0, equSize.width, equSize.height, 0, 0,
						equSize.width, equSize.height, null);
			}
		}
	}

}
