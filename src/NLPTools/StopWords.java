package NLPTools;

import java.util.List;

import org.mongodb.morphia.query.Query;

import databaseTools.Database;
import structures.StopWord;

public class StopWords {
	
	private static StopWords instance;
	private List<StopWord> stopwords;
	
	public static StopWords getInstance() {
		if (instance==null) {
			instance = new StopWords();
		}
		return instance;
	}

	private StopWords() {
		final Query<StopWord> query = Database.getInstance().getStopwordsCollection().createQuery(StopWord.class);
    	this.setStopwords(query.asList());
	}

	public List<StopWord> getStopwords() {
		return stopwords;
	}

	public void setStopwords(List<StopWord> stopwords) {
		this.stopwords = stopwords;
	}

}
