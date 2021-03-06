package edu.lclark.drosophila;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class GraphPanel extends JPanel {

	private static final int DEFAULT_HEIGHT = 600;
	private static final int DEFAULT_WIDTH = 600;

	private AnalyzerPanel analyzerPanel;
	
	/**
	 * 2D array to hold the average fly data for each arena that should be analyzed
	 */
	private double[][] averageVelocity;
	
	/**
	 * array of colors that each arena will use when drawing their data
	 */
	private Color[] colors ={Color.BLUE, Color.red, Color.magenta, Color.cyan, Color.green, Color.YELLOW, Color.orange, Color.pink};
	
	/**
	 * a boolean that should usually be false. true if you want to see large ugly points.
	 */
	private boolean drawPoints;
	
	private int endFrame;
	
	private double frameRate;
	
	private int startFrame;
	
	private String title;
	
	private double videoLength;
	private String xLabel;
	private String yLabel;

	/**
	 * Constructor
	 * @param analyzerPanel
	 * @param drawPoints
	 * @param frameRate
	 * @param title
	 * @param yLabel
	 * @param xLabel
	 */
	public GraphPanel(AnalyzerPanel analyzerPanel, boolean drawPoints, double frameRate, String title, String yLabel, String xLabel) {
		this.analyzerPanel = analyzerPanel;
		this.drawPoints = drawPoints;
		this.frameRate = frameRate;
		this.title = title;
		this.yLabel = yLabel;
		this.xLabel = xLabel;
	}

	public Dimension getMaximumSize() {
		return new Dimension(1000, 1000);
	}
	
	/** Returns the minimum size of this panel as a Dimension object */
	public Dimension getMinimumSize() {
//		return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		return new Dimension((int)(analyzerPanel.getWidth() * (3.0 / 8.0)), (int)(analyzerPanel.getHeight() * 2.0 / 3.0));
	}
	
	public Dimension getPreferredSize() {
		return new Dimension((int)(analyzerPanel.getWidth() * (3.0 / 8.0)), (int)(analyzerPanel.getHeight() * 2.0 / 3.0));
	}
	
	/**
	 * paints the graph in all of its glory
	 */
	public void paintComponent(Graphics g) {	
		if(analyzerPanel.getFlyList().size() <= 0){
			return;
		}
		g.setColor(Color.WHITE);
		int GPanelWidth= this.getWidth();
		int GPanelHeight=this.getHeight();
		g.fillRect(0, 0, GPanelWidth, GPanelHeight);
		if(startFrame == 0 && endFrame == 0)
		{
			averageVelocity = analyzerPanel.getAverageVelocity(analyzerPanel.getRegionsOfInterest());
		}
		else
		{
			averageVelocity = analyzerPanel.getAverageVelocity(analyzerPanel.getRegionsOfInterest(), startFrame, endFrame);
		}
		if(averageVelocity!=null){
			videoLength = averageVelocity[0].length * analyzerPanel.getFrameRate();
		}else{
			videoLength=0;
		}
		g.setColor(Color.BLACK);

		double maxVelocity = 0;

		for (int i = 0; i < averageVelocity.length; i++) {
			for(int j = 0; j<averageVelocity[i].length; j++){
				
				if (averageVelocity[i][j] > maxVelocity) {
				maxVelocity = averageVelocity[i][j];
				}
			}
		}
		
		int leftMarg=100;
		int bottomMarg=100;
		int rightMarg=35;
		int topMarg=50; 
		
		
		int xOffSet = leftMarg-3;
		int yOffSet = GPanelHeight - bottomMarg-3;
		
		double widthOffset = (GPanelWidth - (leftMarg+rightMarg))
				/ (double) (averageVelocity[0].length - 1);
		
		double gridxOffset = 40.0;//(GPanelWidth - (leftMarg+rightMarg)) / 10.0;
		double gridyOffset = 40.0;//(GPanelHeight -(topMarg+bottomMarg))/ 10.0; 
		double heightOffset = (double)(GPanelHeight - (topMarg+bottomMarg)) / maxVelocity;
		Graphics2D g2d = (Graphics2D) g;
		Font f =new Font("SansSarif", Font.PLAIN, 12);
		Rectangle2D bounds; 
		g.setFont(f);
		int stringWidth=0;
		
		DecimalFormat df= new DecimalFormat ("##.##");
//		String.forma
		g.setColor(Color.LIGHT_GRAY);
		
		stringWidth=(int) f.getStringBounds(xLabel, g2d.getFontRenderContext()).getWidth();
		g.drawString(xLabel, (GPanelWidth-stringWidth+leftMarg-rightMarg)/2 , GPanelHeight-(bottomMarg/3));
		
		stringWidth=(int) f.getStringBounds(title, g2d.getFontRenderContext()).getWidth();
		g.drawString(title,(GPanelWidth-stringWidth)/2 , topMarg/3);
		
		// drawing the grid lines and the data for the grid lines
		// Y - AXIS
		double d = videoLength/((GPanelWidth-(leftMarg+rightMarg))/gridxOffset);
		for (int i = 0; i*gridxOffset <= GPanelWidth - (leftMarg + rightMarg); i++) {
			g.drawLine((int)(gridxOffset * i) + leftMarg, GPanelHeight-bottomMarg,
					(int)(gridxOffset * i) + leftMarg, topMarg);
			
			String tempXValue = String.format("%.2f", d*(i + startFrame) / 1000);		
			stringWidth=(int) f.getStringBounds(tempXValue, g2d.getFontRenderContext()).getWidth();		
			g.drawString(tempXValue, (int)(gridxOffset * i) + leftMarg - stringWidth / 2, GPanelHeight-(bottomMarg/2));
		}
		// Y - AXIS
		d = maxVelocity/((GPanelHeight-(topMarg+bottomMarg))/gridyOffset);
		for( int i = 0; i*gridyOffset <= GPanelHeight - (topMarg + bottomMarg); i++) {
			g.drawLine(leftMarg,(int) -(gridyOffset * i) + GPanelHeight - bottomMarg, GPanelWidth-rightMarg,
					(int)	-(gridyOffset * i) + GPanelHeight - bottomMarg);
			
			String tempYValue =String.format("%.2f", d*i);
			int stringHeight=(int) f.getStringBounds(tempYValue, g2d.getFontRenderContext()).getHeight();	
			g.drawString(String.format("%.2f", i * d), leftMarg/2, (int)(-gridyOffset * i) + GPanelHeight - bottomMarg + stringHeight/2);
		}

		g.setColor(Color.BLACK);
		g.drawRect(leftMarg, topMarg, GPanelWidth - (leftMarg+rightMarg), GPanelHeight - (topMarg+bottomMarg));

		
		
if((analyzerPanel.getMovieLoaded() && analyzerPanel.getMovieAnalyzed()) 
		|| (!analyzerPanel.getMovieLoaded() && analyzerPanel.getTotalFrames() >= 2)){
		for (int i = 0; i < averageVelocity.length; i++) {
			
			g.setColor(colors[i%colors.length]);
			for(int j = 0; j< averageVelocity[i].length; j++){
			// draw the points
			if(drawPoints) {
				g.fillOval((int) (xOffSet + j * widthOffset),(int) (yOffSet - averageVelocity[i][j] * heightOffset), 6, 6);
			}
			if (j != 0) {
				g.drawLine(
						(int) (xOffSet + j * widthOffset + 3),
						(int) (yOffSet - averageVelocity[i][j] * heightOffset) + 3,
						(int) (xOffSet + (j - 1) * widthOffset + 3),
						(int) (yOffSet - averageVelocity[i][j-1] * heightOffset + 3));
			}else{
				g.drawString("Arena " + analyzerPanel.getRegionsOfInterest()[i], (GPanelWidth - rightMarg -50), (int) (i*15));
			}
			}
		}
		g.setColor(Color.LIGHT_GRAY);
		g2d.rotate(-Math.PI/2.0);
		g2d.translate(-GPanelHeight,0);
		stringWidth=(int) f.getStringBounds(yLabel, g2d.getFontRenderContext()).getWidth();
		VertDrawString(yLabel,(GPanelHeight-topMarg+bottomMarg-stringWidth)/2 ,leftMarg/3, g2d);
}

	}
	
	public void saveGraph(File file) {
		Container c = this;
		BufferedImage im = new BufferedImage(c.getWidth(), c.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		c.paint(im.getGraphics());
		try {
			ImageIO.write(im, "PNG", file);
		} catch (IOException e) {
			System.err.println("Invalid IO Exception");
			e.printStackTrace();
		}
	}

	public void setDataRange(int start, int end) {
		startFrame = start;
		endFrame = end;
	}

	public void setDrawPoints(boolean drawPoints) {
		this.drawPoints = drawPoints;
	}

	public void setLabels(String titleText, String xAxisText, String yAxisText) {
		title=titleText;
		xLabel=xAxisText;
		yLabel=yAxisText;
	}

	/**
	 * draws a string vertically. this special method is required because
	 * apple reverses the direction of the string and the kerning direction.
	 * silly apple ruining everything.
	 * @param string
	 * @param x
	 * @param y
	 * @param g2d
	 */
	public void VertDrawString(String string, int x, int y, Graphics2D g2d){		
		Font f =new Font("SansSarif", Font.PLAIN, 12);
		Rectangle2D bounds; 
		g2d.setFont(f);
		String temp;
		for(int i= 0; i<=string.length()-1; i++){
			temp= string.substring(i,i+1);
			bounds=f.getStringBounds(temp, g2d.getFontRenderContext());
			g2d.drawString(temp, x, y);
			x+=bounds.getWidth();
		}

	}
}
