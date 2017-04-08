package structures;

public class QuestionToUser
{
    private String question;
    private boolean yesNo;
    
    public QuestionToUser() {
    	
    }

    public QuestionToUser(String question, boolean yesNo)
    {
        this.question = question;
        this.yesNo = yesNo;
    }

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public boolean isYesNo() {
		return yesNo;
	}

	public void setYesNo(boolean yesNo) {
		this.yesNo = yesNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((question == null) ? 0 : question.hashCode());
		result = prime * result + (yesNo ? 1231 : 1237);
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
		QuestionToUser other = (QuestionToUser) obj;
		if (question == null) {
			if (other.question != null)
				return false;
		} else if (!question.equals(other.question))
			return false;
		if (yesNo != other.yesNo)
			return false;
		return true;
	}
	
	
}
