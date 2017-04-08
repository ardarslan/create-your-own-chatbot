package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bson.types.ObjectId;

import NLPTools.RootFinder;
import NLPTools.StringSimilarity;
import databaseTools.Database;
import enums.StateEnum;
import structures.AnswerToUser;
import structures.Form;
import structures.KeywordsAndAnswer;
import structures.PartialKeywordsAndAnswerWithQuestion;
import structures.QuestionToUser;

public class ChatWithBotController {

	AnswerToUser currentAnswerToUser;
	String answerToReturn;

	boolean isPartial;
	boolean isForm;

	ArrayList<PartialKeywordsAndAnswerWithQuestion> currentListOfPartialKeywordsAndAnswerWithQuestions;
	QuestionToUser currentQuestionToUser;
	HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>> partialMatches;
	Form currentEmptyForm;
	Form currentFullForm;

	public ChatWithBotController() {
		AnswerToUser answerToUser = new AnswerToUser();
		answerToUser.getAnswerToUserQuestion("init");
		currentAnswerToUser = new AnswerToUser();
		isPartial=false;
		isForm=false;
	}

	public void answerToUserQuestion(String question) {
		if (!isPartial) {
			if (!isForm) {
				currentAnswerToUser.getAnswerToUserQuestion(question);
				System.out.println(currentAnswerToUser.getState());
				if (currentAnswerToUser.getState().equals(StateEnum.FULL_MATCH)) {
					String temp = currentAnswerToUser.getFullMatches().get(0).getAnswer();
					if (temp.substring(0, 5).equals("&Form")) {
						setForm(true);
						setCurrentEmptyForm(Database.getInstance().FormCorrespondingToID(temp.substring(5)));
						setCurrentFullForm(getCurrentEmptyForm());
						getCurrentFullForm().set_id(new ObjectId());
						answerToReturn = currentEmptyForm.getQuestions().get(0);
						currentEmptyForm.getQuestions().remove(0);
						currentAnswerToUser.setFullMatches(new ArrayList<KeywordsAndAnswer>());
						setPartial(false);
					}
					else {
						answerToReturn = temp + " Size başka nasıl yardımcı olabilirim?";
						currentAnswerToUser.setFullMatches(new ArrayList<KeywordsAndAnswer>());
						setPartial(false);
					}
				}
				else if (currentAnswerToUser.getState().equals(StateEnum.FULL_MATCH_WITH_TYPO)) {
					String temp = currentAnswerToUser.getFullMatchesWithTypos().get(0).getAnswer();
					if (temp.substring(0, 5).equals("&Form")) {
						setForm(true);
						setCurrentEmptyForm(Database.getInstance().FormCorrespondingToID(temp.substring(5)));
						setCurrentFullForm(getCurrentEmptyForm());
						answerToReturn = currentEmptyForm.getQuestions().get(0);
						currentEmptyForm.getQuestions().remove(0);
						currentAnswerToUser.setFullMatchesWithTypos(new ArrayList<KeywordsAndAnswer>());
						setPartial(false);
					}
					else {
						answerToReturn = temp + " Size başka nasıl yardımcı olabilirim?";
						currentAnswerToUser.setFullMatchesWithTypos(new ArrayList<KeywordsAndAnswer>());
						setPartial(false);
					}
				}
				else if (currentAnswerToUser.getState().equals(StateEnum.PARTIAL_MATCH)) {
					partialMatches = currentAnswerToUser.getPartialMatches();
					findCurrentListOfPartialKeywordsAndAnswerWithQuestion(partialMatches);
					setPartialMatches(partialMatches);
					setAnswerToReturn(currentQuestionToUser.getQuestion());
					setPartial(true);
				}
				else if (currentAnswerToUser.getState().equals(StateEnum.PARTIAL_MATCH_WITH_TYPO)) {
					partialMatches = currentAnswerToUser.getPartialMatchesWithTypos();
					findCurrentListOfPartialKeywordsAndAnswerWithQuestion(partialMatches);
					setPartialMatches(partialMatches);
					setAnswerToReturn(currentQuestionToUser.getQuestion());
					setPartial(true);
				}
				else {
					answerToReturn = "Sizi anlayamadım. Son söylediğinizi başka bir şekilde ifade edebilir misiniz?";
					setPartial(false);
				}
			}
			else {
				if (currentEmptyForm.getQuestions().isEmpty()) {
					currentFullForm.getAnswers().add(question);
					setForm(false);
					Database.getInstance().insertFullFormToDatabase(currentFullForm);
					answerToReturn = "Talebiniz başarıyla kaydedildi. Personelimiz tarafından yapılacak inceleme sonucunda size en kısa sürede dönüş yapılacaktır. Size başka nasıl yardımcı olabilirim?";
				}
				else {
					currentFullForm.getAnswers().add(question);
					answerToReturn = currentEmptyForm.getQuestions().get(0);
					currentEmptyForm.getQuestions().remove(0);
				}
			}
		}
		else {
			if (currentQuestionToUser.isYesNo()) {
				String shortAnswer = question.toLowerCase();
				// TODO
				if (shortAnswer.contains("evet")) {
					// TODO
					String temp = currentListOfPartialKeywordsAndAnswerWithQuestions.get(0).getKeywordsAndAnswer().getAnswer();
					if (temp.substring(0, 5).equals("&Form")) {
						setForm(true);
						setCurrentEmptyForm(Database.getInstance().FormCorrespondingToID(temp.substring(5)));
						setCurrentFullForm(getCurrentEmptyForm());
						answerToReturn = currentEmptyForm.getQuestions().get(0);
						currentEmptyForm.getQuestions().remove(0);
						setPartial(false);
					}
					else {
						answerToReturn = temp + " Size başka nasıl yardımcı olabilirim?";
						setPartial(false);
					}
				}
				else {
					findCurrentListOfPartialKeywordsAndAnswerWithQuestion(partialMatches);
					if (currentListOfPartialKeywordsAndAnswerWithQuestions!=null && !currentListOfPartialKeywordsAndAnswerWithQuestions.isEmpty()) {
						answerToReturn = currentQuestionToUser.getQuestion();
						setPartial(true);
					}
					else {
						setPartial(false);
						answerToReturn = "O halde söylediğinizi anlayamamışım. En baştan başlayabilir miyiz?";
					}
				}
			}
			else {
				String shortAnswer = question.toLowerCase();
				ArrayList<String> roots = RootFinder.getInstance().findRootsOfSentence(shortAnswer);
				// TODO
				boolean found = false;

				for (PartialKeywordsAndAnswerWithQuestion p : currentListOfPartialKeywordsAndAnswerWithQuestions) {
					if (found) break;
					ArrayList<String> missingSynonyms = p.getMissingSynonyms();
					StringSimilarity similarity = new StringSimilarity();
					for (String root : roots) {
						if (found) break;
						for (String synonym : missingSynonyms) {
							if (found) break;
							if (similarity.execute(root, synonym)<=1) {
								String temp = p.getKeywordsAndAnswer().getAnswer();
								if (temp.substring(0, 5).equals("&Form")) {
									setForm(true);
									setCurrentEmptyForm(Database.getInstance().FormCorrespondingToID(temp.substring(5)));
									setCurrentFullForm(getCurrentEmptyForm());
									answerToReturn = currentEmptyForm.getQuestions().get(0);
									currentEmptyForm.getQuestions().remove(0);
									setPartial(false);
									found = true;
								}
								else {
									answerToReturn = p.getKeywordsAndAnswer().getAnswer() + " Size başka nasıl yardımcı olabilirim?";
									setPartial(false);
									found = true;
								}
							}
						}
					}
				}

				if (!found) {
					findCurrentListOfPartialKeywordsAndAnswerWithQuestion(partialMatches);
					if (currentListOfPartialKeywordsAndAnswerWithQuestions!=null && !currentListOfPartialKeywordsAndAnswerWithQuestions.isEmpty()) {
						answerToReturn = currentQuestionToUser.getQuestion();
						setPartial(true);
					}
					else {
						answerToReturn = "Sizi anlayamadım. En baştan başlayabilir miyiz?";
						setPartial(false);
					}
				}
			}
		}
	}

	private void findCurrentListOfPartialKeywordsAndAnswerWithQuestion(HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>> hashmap) {
		ArrayList<PartialKeywordsAndAnswerWithQuestion> result = null;
		QuestionToUser questionToUser = null;

		Iterator it = hashmap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			QuestionToUser aQuestionToUser = (QuestionToUser)pair.getKey();
			if (!aQuestionToUser.isYesNo() && !hashmap.get(aQuestionToUser).isEmpty()) {
				questionToUser = aQuestionToUser;
				result = hashmap.get(aQuestionToUser);
				it.remove();
				break;
			}
		}

		if (result==null) {
			Iterator it2 = hashmap.entrySet().iterator();
			while (it2.hasNext()) {
				Map.Entry pair = (Map.Entry)it2.next();
				QuestionToUser aQuestionToUser = (QuestionToUser)pair.getKey();
				if (!hashmap.get(aQuestionToUser).isEmpty()) {
					questionToUser = aQuestionToUser;
					result = hashmap.get(aQuestionToUser);
					it2.remove();
					break;
				}
			}
			this.currentQuestionToUser = questionToUser;
			this.currentListOfPartialKeywordsAndAnswerWithQuestions = result;
		}
		else  {
			this.currentQuestionToUser = questionToUser;
			this.currentListOfPartialKeywordsAndAnswerWithQuestions = result;
		}
	}

	public QuestionToUser getCurrentQuestionToUser() {
		return currentQuestionToUser;
	}

	public void setCurrentQuestionToUser(QuestionToUser currentQuestionToUser) {
		this.currentQuestionToUser = currentQuestionToUser;
	}

	public AnswerToUser getCurrentAnswerToUser() {
		return currentAnswerToUser;
	}

	public void setCurrentAnswerToUser(AnswerToUser currentAnswerToUser) {
		this.currentAnswerToUser = currentAnswerToUser;
	}

	public String getAnswerToReturn() {
		return answerToReturn;
	}

	public void setAnswerToReturn(String answerToReturn) {
		this.answerToReturn = answerToReturn;
	}

	public boolean isPartial() {
		return isPartial;
	}

	public void setPartial(boolean isPartial) {
		this.isPartial = isPartial;
	}

	public boolean isForm() {
		return isForm;
	}

	public void setForm(boolean isForm) {
		this.isForm = isForm;
	}

	public Form getCurrentEmptyForm() {
		return currentEmptyForm;
	}

	public void setCurrentEmptyForm(Form currentForm) {
		this.currentEmptyForm = currentForm;
	}

	public Form getCurrentFullForm() {
		return currentFullForm;
	}

	public void setCurrentFullForm(Form currentFullForm) {
		this.currentFullForm = currentFullForm;
	}

	public HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>> getPartialMatches() {
		return partialMatches;
	}

	public void setPartialMatches(HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>> partialMatches) {
		this.partialMatches = partialMatches;
	}

	public ArrayList<PartialKeywordsAndAnswerWithQuestion> getCurrentListOfPartialKeywordsAndAnswerWithQuestions() {
		return currentListOfPartialKeywordsAndAnswerWithQuestions;
	}

	public void setCurrentListOfPartialKeywordsAndAnswerWithQuestions(
			ArrayList<PartialKeywordsAndAnswerWithQuestion> currentListOfPartialKeywordsAndAnswerWithQuestions) {
		this.currentListOfPartialKeywordsAndAnswerWithQuestions = currentListOfPartialKeywordsAndAnswerWithQuestions;
	}



}
