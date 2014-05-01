package edu.lclark.drosophila;

import java.awt.Dimension;

import javax.swing.*;

public class DataTabbs extends JTabbedPane{
	AnalyzerPanel analyzer;
	public DataTabbs(AnalyzerPanel a){
		addTab("Single", null, new DataPanel(a), "shows data associated with individual flies");
		addTab("Group", null, new GroupDataPanel(a), "Shows data associated with groups of flies");
		addTab("Arena's", null, new ArenaDataPanel(a), "Shows data associated with groups of flies");
	}

	/**
	 * sets this panels default size
	 */
	public Dimension getPreferredSize(){
		return new Dimension(400,400);
	}
	
	/**
	 * sets this panels minimum size
	 */
	public Dimension getMinimumSize(){
		return new Dimension(400,400);
	}
	
	public String getDataForFile(){
		return ((DataPanel)this.getComponent(0)).getDataForFile();
	}
}
