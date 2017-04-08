package structures;

import java.util.ArrayList;

public class PartialKeywordsAndAnswerWithQuestion {

	private KeywordsAndAnswer keywordsAndAnswer;
    private QuestionToUser questionToUser;
    private ArrayList<String> missingSynonyms;

    public PartialKeywordsAndAnswerWithQuestion(KeywordsAndAnswer keywordsAndAnswer, QuestionToUser questionToUser, ArrayList<String> missingSynonyms){
        this.keywordsAndAnswer = keywordsAndAnswer;
        this.questionToUser = questionToUser;
        this.setMissingSynonyms(missingSynonyms);
    }

	public KeywordsAndAnswer getKeywordsAndAnswer() {
		return keywordsAndAnswer;
	}

	public void setKeywordsAndAnswer(KeywordsAndAnswer keywordsAndAnswer) {
		this.keywordsAndAnswer = keywordsAndAnswer;
	}

	public QuestionToUser getQuestionToUser() {
		return questionToUser;
	}

	public void setQuestionToUser(QuestionToUser questionToUser) {
		this.questionToUser = questionToUser;
	}

	public ArrayList<String> getMissingSynonyms() {
		return missingSynonyms;
	}

	public void setMissingSynonyms(ArrayList<String> missingSynonyms) {
		this.missingSynonyms = missingSynonyms;
	}
    
    
	
}
