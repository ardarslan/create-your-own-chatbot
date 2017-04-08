package structures;

public class InitializationThread extends Thread {

	public void run(){
		AnswerToUser answerToUser = new AnswerToUser();
		answerToUser.getAnswerToUserQuestion("init");
	}

}
