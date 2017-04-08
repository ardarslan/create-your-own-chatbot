package controllers;

import java.util.ArrayList;

import structures.AnswerToUser;
import structures.KeywordsAndAnswer;

public class SearchEngineController {
	private ArrayList<String> questions;
	private ArrayList<String> answers;
	private AnswerToUser answerToUser;

	public SearchEngineController() {
		questions = new ArrayList<String>();
		answers = new ArrayList<String>();
		answerToUser = new AnswerToUser();
	}

	public void searchAtDatabase(String question) {
		answerToUser = answerToUser.getAnswersToSearch(question);
		ArrayList<KeywordsAndAnswer> fullMatches = answerToUser.getFullMatches();
		ArrayList<KeywordsAndAnswer> fullMatchesWithTypos = answerToUser.getFullMatchesWithTypos();

		for (KeywordsAndAnswer keywordsAndAnswer : fullMatches) {
			String newQuestion = keywordsAndAnswer.getQuestion();
			String newAnswer = keywordsAndAnswer.getAnswer();
			if (!questions.contains(newQuestion) && !newAnswer.substring(0,5).equals("&Form")) {
				questions.add(keywordsAndAnswer.getQuestion());
				answers.add(keywordsAndAnswer.getAnswer());
			}
		}

		for (KeywordsAndAnswer keywordsAndAnswer : fullMatchesWithTypos) {
			String newQuestion = keywordsAndAnswer.getQuestion();
			String newAnswer = keywordsAndAnswer.getAnswer();
			if (!questions.contains(newQuestion) && !newAnswer.substring(0,5).equals("&Form")) {
				questions.add(keywordsAndAnswer.getQuestion());
				answers.add(keywordsAndAnswer.getAnswer());
			}
		}
	}

	public ArrayList<String> getQuestions() {
		return questions;
	}

	public void setQuestions(ArrayList<String> questions) {
		this.questions = questions;
	}

	public ArrayList<String> getAnswers() {
		return answers;
	}

	public void setAnswers(ArrayList<String> answers) {
		this.answers = answers;
	}

	public AnswerToUser getAnswerToUser() {
		return answerToUser;
	}

	public void setAnswerToUser(AnswerToUser answerToUser) {
		this.answerToUser = answerToUser;
	}
}
