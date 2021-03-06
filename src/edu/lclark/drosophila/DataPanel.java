package edu.lclark.drosophila;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class DataPanel extends JPanel {

	protected AnalyzerPanel analyzerPanel;
	protected List<Fly> flyList;
	protected JTextArea text;

	/**
	 * creates this panel which at the moment displays all the data for the
	 * analytical stories we want to show
	 * 
	 * @param analyzerpanel
	 */
	public DataPanel(AnalyzerPanel analyzerpanel) {

		this.analyzerPanel = analyzerpanel;
		flyList = analyzerpanel.getFlyList();
		text = new JTextArea(7, 30);
		JScrollPane scrollpane = new JScrollPane(text);
		add(scrollpane);
		text.append(createLabels());
	}

	/**
	 * Overloaded version of createData that automatically calls it for the data
	 * panel (not for saving to file), for ease of use
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public String createData(int start, int end) {
		return createData(start, end, false);
	}

	/**
	 * Creates the text block that contains the data for all the flies in the
	 * fly list
	 * 
	 * @param start
	 * @param end
	 * @param savingToFile
	 *            if data is to be saved to file (puts it in .csv format)
	 * @return String Data
	 */
	public String createData(int start, int end, boolean savingToFile) {
		String data = "";
		if (analyzerPanel.getFlyList() != null) {
			for (Fly fly : analyzerPanel.getFlyList()) {
				data += createFlyLine(fly, start, end, savingToFile);
				data += "\n";
			}
		}
		return data;
	}

	/**
	 * takes a single fly and creates a string to display its avg velocity and
	 * total distance"
	 * 
	 * @param fly
	 * @param start
	 * @param end
	 * @return String FlyLine
	 */
	public String createFlyLine(Fly fly, int start, int end,
			boolean savingToFile) {
		String data = "";
		if (savingToFile) {
			data = String.format("%-15s, %-25f, %-5f", fly.toString(),
					fly.averageVelFly(start - 1, end - 1),
					fly.totalDistance(start - 1, end - 1));
		} else {
			data = String.format("%-15s %-25f %-5f", fly.toString(),
					fly.averageVelFly(start - 1, end - 1),
					fly.totalDistance(start - 1, end - 1));
		}
		return data;
	}

	/**
	 * Overloaded version of create labels, which calls the version of create
	 * labels for the data panel.
	 * 
	 * @return string of labels for data panel
	 */
	public String createLabels() {
		return createLabels(false);
	}

	/**
	 * creates the String that labels our data
	 * 
	 * @param savingToFile
	 *            if data is being saved to file, adds commas to make it .csv
	 *            format
	 * @return labels
	 */
	public String createLabels(boolean savingToFile) {
		String labels = "";
		if (savingToFile) {
			labels = String.format("%-15s %-25s %-5s \n", "fly,",
					"average velocity,", "total distance");
		} else {
			labels = String.format("%-15s %-25s %-5s \n", "fly",
					"average velocity", "total distance");
		}
		return labels;
	}

	/**
	 * passes on a string showing important file data
	 * @return
	 */
	public String getDataForFile() {
		String data = createLabels(true);
		data += createData(1, analyzerPanel.getTotalFrames(), true);
		return data;
	}

	/**
	 * sets this panels minimum size
	 */
	public Dimension getMinimumSize() {

		// return new Dimension(400,400);
		return new Dimension((int) (analyzerPanel.getWidth() * (3.0 / 8.0)),
				(int) (analyzerPanel.getHeight() / 3.0));
	}

	/**
	 * retruns a boolean saying whether the movie has been loaded
	 * @return
	 */
	public boolean getMovieLoaded() {
		return analyzerPanel.getMovieLoaded();
	}

	/**
	 * sets this panels default size
	 */
	public Dimension getPreferredSize() {

		// return new Dimension(400,400);
		return new Dimension((int) (analyzerPanel.getWidth() * (3.0 / 8.0)),
				(int) (analyzerPanel.getHeight() / 3.0));
	}

	/**
	 * the paint component for this piece. Directly tied into the fly list.
	 */
	public void paintComponent(Graphics G) {
		if (analyzerPanel.getFlyList().size() > 0) {
			text.setText(createLabels());
			if (getMovieLoaded()) {
				text.append(createData(1, analyzerPanel.getTotalFrames() - 1));
			} else {
				text.append(createData(1, analyzerPanel.getTotalFrames()));
			}
		}
	}

}
