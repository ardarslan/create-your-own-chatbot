package controllers;

import databaseTools.Database;

public class AddNewStopWordController {
	
	public AddNewStopWordController() {
		
	}
	
	public void insertNewStopWordToDatabase(String word) {
		Database.getInstance().insertStopwordToDatabase(word);
	}
}