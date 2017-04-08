package structures;

import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

public class StopWord {
	
	@Id
    @Property("id")
	private String stopWord;

	public StopWord() {
		
	}
	
	public StopWord(String stopWord) {
		this.stopWord = stopWord;
	}

	public String getStopWord() {
		return stopWord;
	}

	public void setStopWord(String stopWord) {
		this.stopWord = stopWord;
	}
}
