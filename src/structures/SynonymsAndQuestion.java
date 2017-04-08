package structures;

import java.util.ArrayList;

public class SynonymsAndQuestion
{
    private QuestionToUser questionToUser;
    private ArrayList<String> synonyms;
    
    public SynonymsAndQuestion() {
    	
    }

    public SynonymsAndQuestion(ArrayList<String> synonyms, String question, boolean yesNo)
    {
        this.synonyms = synonyms;
        questionToUser = new QuestionToUser(question, yesNo);
    }
    
    public QuestionToUser getQuestionToUser() {
		return questionToUser;
	}

	public void setQuestionToUser(QuestionToUser questionToUser) {
		this.questionToUser = questionToUser;
	}

	public ArrayList<String> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(ArrayList<String> synonyms) {
		this.synonyms = synonyms;
	}

	
}
