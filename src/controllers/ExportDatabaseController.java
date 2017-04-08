package controllers;

import fileIO.XMLFileSaver;

public class ExportDatabaseController {
	
	public ExportDatabaseController() {
		
	}
	
	public void exportDatabase(String path) {
		new XMLFileSaver().saveFile(path);
	}
}
