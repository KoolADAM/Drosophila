package edu.lclark.drosophila;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

public class AnalyzerPanel extends JPanel {

	/**
	 * The AnalyzerGui object which this AnalyzerPanel communicates with.
	 */
	private AnalyzerGui gui;
	
	/**
	 * The GraphPanel object which this AnalyzerPanel communicates with.
	 */
	private GraphPanel graphPanel;

	/**
	 * The ImagePanel object which this AnalyzerPanel communicates with.
	 */
	private ImagePanel ipanel;
	/**
	 * The Data Panel object which this AnalyzerPanel communicates with.
	 */
	private DataPanel dpanel;

	/**
	 * The constructor for AnalyzerPanel, which adds the button panel and image
	 * panel to this panel.
	 * 
	 * @param gui
	 *            the AnalyzerGui which this AnalyzerPanel has to communicate
	 *            with.
	 */
	AnalyzerPanel(AnalyzerGui gui) {
		this.gui = gui;
		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();		
		
		ipanel = new ImagePanel(this);
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.anchor = constraints.EAST;
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridheight = 3;
		constraints.gridwidth = 1;
		add(ipanel, constraints);
		
		
		dpanel = new DataPanel(this);
		constraints.anchor = constraints.NORTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth= 1;
		constraints.weighty=1;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 1;
		//add(dpanel, constraints);
		constraints.gridheight = 1;
		add(dpanel, constraints);
		
		graphPanel = new GraphPanel(this, false, .10, "TITLE", "vertical label now this is longer ", "Xkljhfdsalkjfhasdkljfh" );
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridheight = 1;
		//constraints.insets = new Insets(0, 50, 50, 0);
		add(graphPanel, constraints);
	}

	public double[] getAverageVelocity() {
		return gui.getAverageVelocity();


	}

	/**
	 * Removes the currently attached images and fly data from the Analyzer.
	 */
	public void clearImages() {
		gui.clearImages();

	}

	/**
	 * Increments the displayed image index in ImagePanel by 1.
	 */
	public void decrementIndex() {
		ipanel.decrementIndex();
	}

	/**
	 * Gets the List of Fly objects, which contain all gathered data, from the
	 * Analyzer.
	 * 
	 * @return a List of identified Fly objects.
	 */
	public List<Fly> getFlyList() {
		if(gui.getFlies()==null){
			return new LinkedList<Fly>();
		}
		return gui.getFlies();
	}

	/**
	 * Getter for the total number of frames or images which have been processed
	 * by the Analyzer.
	 * 
	 * @return the total number of frames or images which have been processed.
	 */
	public int getTotalFrames() {
		return gui.getTotalFrames();
	}

	/**
	 * Increments the displayed image index in ImagePanel by 1.
	 */
	public void incrementIndex() {
		ipanel.incrementIndex();
	}
	
	public void displayMessagePopup(String s) {
		JOptionPane.showMessageDialog(null, s, "Error", 
				JOptionPane.INFORMATION_MESSAGE, 
				new ImageIcon(getClass().getResource("images/DrawFlyTrajectoriesToggle.png")));
	}

	/**
	 * Returns the file path of specified image that is stored in the Analyzer.
	 * 
	 * @param index
	 *            the index of the image that is desired.
	 * @return the String containing the file path of the image specified.
	 */
	public String passdownImage(int index) {
		File file = gui.passDownImage(index);
		if (file != null) {
			return file.getPath();
		}
		return null;
	}

	/**
	 * Passes a File containing an image to the Analyzer, which will identify
	 * any flies within the image.
	 * 
	 * @param file
	 *            the file containing the image that needs to be identified.
	 */
	public void passImage(File file) {
		gui.passImage(file);
	}

	public void setDrawTrajectories(int startFrame, int endFrame) {
		ipanel.setDrawTrajectories(startFrame, endFrame);

	}

	/**
	 * Toggles the identifying dots drawn over the identified flies on the gui.
	 */
	public void setFlydentifiers() {
		ipanel.setFlydentifiers();
	}

	/**
	 * Updates the size threshold in Analyzer. This is used to determine if an
	 * object identified within an image is large enough to be considered a fly.
	 * This will also analyze all stored images again.
	 * 
	 * @param input
	 *            the value which size threshold will be set to.
	 */
	public void sizeThresholdUpdate(int input) {
		gui.sizeThresholdUpdate(input);
	}
	
	/**
	 * Daisy chain method to pass an opened movie file
	 * @param file
	 */
	public void passMovie(File file) {
		gui.passMovie(file);
	}

	
	/**
	 * Shows a loaded movie after clicking the "Open a movie" button. 
	 * @param frames
	 * 	         All of the frames of the selected file
	 * @param l
	 * 	      The wait time in milliseconds for a thread to sleep. 
	 */
	public void showMovie(final List<BufferedImage> frames, final long l) {
		System.out.println("l is " + l);
		ipanel.setMoviePlaying(true);
		new Thread(new Runnable() {
			public void run() {
				for (BufferedImage b : frames) {
					final BufferedImage image = b;
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								ipanel.setMoviePlaying(true);
								ipanel.setImage(image);
								ipanel.paintImmediately(ipanel.getVisibleRect());
								Thread.sleep(l);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							ipanel.setMoviePlaying(false);
						}
					});
				}
				
			}
		}
				).start();
//		ipanel.setMoviePlaying(false);
	}
	
	public void setMoviePlaying(boolean b){
		ipanel.setMoviePlaying(b);
	}

	public void setImageContrast(double d) {
		gui.setImageContrast(d);		
	}

	public double getImageContrast() {
		return gui.getImageContrast();
	}

	public File passdownFile(int imageIndex) {
		return gui.passdownFile(imageIndex);
	}
	
	public void contrastThresholdUpdate(int input) {
		gui.contrastThresholdUpdate(input);

	}

	public BufferedImage getFirstFrameFromMovie() {
		// TODO Auto-generated method stub
		return gui.getFirstFrameFromMovie();
	}

	public void setMovieLoading(boolean b) {
		ipanel.setMovieLoading(b);
		
	}

	public boolean getMovieLoaded() {
		// TODO Auto-generated method stub
		return gui.getMovieLoaded();
	}

	public void analyzeMovie(int sampleRate) {
		gui.analyzeMovie(sampleRate);
		
	}
}
