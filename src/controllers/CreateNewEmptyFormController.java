package controllers;

import java.util.ArrayList;

import NLPTools.RootFinder;
import databaseTools.Database;
import enums.StringTypeEnum;
import structures.Entity;
import structures.Form;
import structures.KeywordsAndAnswer;
import structures.QuestionToUser;
import structures.SynonymsAndQuestion;

public class CreateNewEmptyFormController {

	KeywordsAndAnswer keywordsAndAnswer;
	Form emptyForm;

	public CreateNewEmptyFormController() {

	}

	public void createNewKeywordsAndAnswer(String sentence, ArrayList<String> selectedKeywords, String answer) {
		keywordsAndAnswer = (new CreateNewKeywordsAndAnswerController()).createNewKeywordsAndAnswer(sentence, selectedKeywords, answer);
	}

	public void createNewEmptyForm(ArrayList<String> questions, ArrayList<StringTypeEnum> stringTypes) {
		emptyForm = new Form(questions, new ArrayList<String>(), stringTypes);
	}

	public void insertAllToDatabase() {
		for (SynonymsAndQuestion synonymsAndQuestion : keywordsAndAnswer.getKeywords()) {
			QuestionToUser questionToUser = synonymsAndQuestion.getQuestionToUser();
			String question = questionToUser.getQuestion();
			ArrayList<String> rootsOfQuestion = RootFinder.getInstance().findRootsOfSentence(question);
			if (question == null || question.equals("")) {
				questionToUser.setQuestion(keywordsAndAnswer.getQuestion());
			}
			else if (question.contains("Söylediğinizi şöyle anladım")) {
				questionToUser.setYesNo(true);
			}
			else if (rootsOfQuestion.contains("mi") || rootsOfQuestion.contains("mı") || rootsOfQuestion.contains("mü") || rootsOfQuestion.contains("mu")) {
				questionToUser.setYesNo(true);
			}
			else questionToUser.setYesNo(false);

			if (synonymsAndQuestion.getSynonyms().isEmpty()) {
				keywordsAndAnswer.getKeywords().remove(synonymsAndQuestion);
			}
		}
		Database.getInstance().insertEmptyFormToDatabase(emptyForm, keywordsAndAnswer);
	}


	public KeywordsAndAnswer getKeywordsAndAnswer() {
		return keywordsAndAnswer;
	}

	public void setKeywordsAndAnswer(KeywordsAndAnswer keywordsAndAnswer) {
		this.keywordsAndAnswer = keywordsAndAnswer;
	}

	public Form getEmptyForm() {
		return emptyForm;
	}

	public void setEmptyForm(Form emptyForm) {
		this.emptyForm = emptyForm;
	}
}
