package structures;

import java.util.ArrayList;

import org.bson.types.ObjectId;

public class KeywordsAndAnswer {

	private String question; 
	private ArrayList<SynonymsAndQuestion> keywords;
	private String answer;
	private ObjectId _id = new ObjectId();

	public KeywordsAndAnswer() {

	}

	public KeywordsAndAnswer(ArrayList<SynonymsAndQuestion> keywords, String answer, String question)
	{
		this.keywords = keywords;
		this.answer = answer;  
		this.question = question;
	}

	public ArrayList<SynonymsAndQuestion> getKeywords() {
		return keywords;
	}

	public void setKeywords(ArrayList<SynonymsAndQuestion> keywords) {
		this.keywords = keywords;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}   

	public KeywordsAndAnswer deepClone() {
		ArrayList<SynonymsAndQuestion> newList = new ArrayList<SynonymsAndQuestion>();
		KeywordsAndAnswer result = new KeywordsAndAnswer(newList, getAnswer(), getQuestion());
		for (SynonymsAndQuestion synonymsAndQuestion : getKeywords()) {
			newList.add(new SynonymsAndQuestion(synonymsAndQuestion.getSynonyms(), synonymsAndQuestion.getQuestionToUser().getQuestion(), synonymsAndQuestion.getQuestionToUser().isYesNo()));
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeywordsAndAnswer other = (KeywordsAndAnswer) obj;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		return true;
	}
}
