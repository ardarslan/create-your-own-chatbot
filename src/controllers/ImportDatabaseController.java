package controllers;

import java.io.File;

import fileIO.XMLFileLoader;
import fileIO.XMLFileValidator;

public class ImportDatabaseController {
	
	public ImportDatabaseController() {
		
	}
	
	public String importDatabase(String path) {
		File file = new File(path);
		String isValid = new XMLFileValidator().isValid(file);
		if (isValid.equals("valid")) {
			new XMLFileLoader().loadFile(file);
			System.out.println("File loaded");
			return "valid";
		}
		else {
			System.out.println(isValid);
			return isValid;
		}
	}
}
