package controllers;

import java.util.ArrayList;
import java.util.List;

import NLPTools.RootFinder;
import NLPTools.StopWords;
import NLPTools.StringSimilarity;
import databaseTools.Database;
import structures.AnswerToUser;
import structures.Entity;
import structures.KeywordsAndAnswer;
import structures.QuestionToUser;
import structures.StopWord;
import structures.SynonymsAndQuestion;

public class CreateNewKeywordsAndAnswerController {

	KeywordsAndAnswer keywordsAndAnswer;

	public CreateNewKeywordsAndAnswerController() {
		AnswerToUser answerToUser = new AnswerToUser();
	}

	public KeywordsAndAnswer createNewKeywordsAndAnswer(String sentence, ArrayList<String> selectedKeywords, String answer) {
		String normalizedSentence = onlyDigitsLettersAndWhiteSpacesAllowedAndToLowerCase(sentence); //sentence normalized
		if (selectedKeywords == null || selectedKeywords.isEmpty()) {
			selectedKeywords = new ArrayList<String>();
			String[] array = normalizedSentence.split(" ");
			for (String word : array) {
				selectedKeywords.add(word);
			}
		}

		for (int i=0; i<selectedKeywords.size(); i++) {
			String current = selectedKeywords.get(i);
			selectedKeywords.set(i, onlyDigitsAndLettersAllowedAndToLowerCase(current)); //selectedKeywords normalized
		}
		ArrayList<Integer> indexesOfSelectedKeywords = indexesOfSelectedKeywords(normalizedSentence, selectedKeywords);
		ArrayList<String> roots = RootFinder.getInstance().findRootsOfSentence(normalizedSentence);

		ArrayList<String> rootsOfSelectedKeywords = new ArrayList<String>();
		for (Integer i : indexesOfSelectedKeywords) {
			rootsOfSelectedKeywords.add(roots.get(i));
		}

		List<StopWord> stopWordList = StopWords.getInstance().getStopwords();
		ArrayList<String> deletedRoots = new ArrayList<String>();
		for (String currentRoot : rootsOfSelectedKeywords) {
			for (StopWord s : stopWordList) {
				String stopWord = s.getStopWord();
				if (stopWord.equals(currentRoot)) {
					deletedRoots.add(currentRoot);
					break;
				}
			}
		}
		for (String root : deletedRoots) {
			rootsOfSelectedKeywords.remove(root);
		}

		KeywordsAndAnswer keywordsAndAnswer = new KeywordsAndAnswer(new ArrayList<SynonymsAndQuestion>(), answer, sentence);
		for (String root : rootsOfSelectedKeywords) {
			String existingQuestionInDatabase = Database.getInstance().getExistingQuestionForMissingRootFromDatabase(root, rootsOfSelectedKeywords);
			SynonymsAndQuestion synonymsAndQuestion;
			if (existingQuestionInDatabase != null) {
				synonymsAndQuestion = new SynonymsAndQuestion(new ArrayList<String>(),
						existingQuestionInDatabase,
						true);
			}
			else {
				synonymsAndQuestion = new SynonymsAndQuestion(new ArrayList<String>(),
						"Söylediğinizi şöyle anladım: '" + sentence + "' Doğru anlamış mıyım?",
						true);
			}

			synonymsAndQuestion.getSynonyms().add(root);

			ArrayList<String> existingSynonyms = Database.getInstance().getExistingSynonymsForAWordFromDatabase(root, rootsOfSelectedKeywords);

			if (existingSynonyms != null && !existingSynonyms.isEmpty()) {
				synonymsAndQuestion.getSynonyms().addAll(existingSynonyms);
			}
			else {
				ArrayList<String> threeSynonymsOfWord = Database.getInstance().getAllSynonymsOfAWordFromDatabase(root);
				for (String synonym : threeSynonymsOfWord) {
					synonymsAndQuestion.getSynonyms().add(synonym);
				}
			}
			keywordsAndAnswer.getKeywords().add(synonymsAndQuestion);
		}
		this.keywordsAndAnswer = keywordsAndAnswer;
		return keywordsAndAnswer;
	}

	public void insertKeywordsAndAnswerToDatabase(KeywordsAndAnswer keywordsAndAnswer) {
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
		if (keywordsAndAnswer.getAnswer() == null || keywordsAndAnswer.getAnswer().equals("")) {
			System.out.println("Empty Answer");
		}
		else Database.getInstance().insertKeywordsAndAnswerToDatabase(keywordsAndAnswer);
	}

	private ArrayList<Integer> indexesOfSelectedKeywords(String sentence, ArrayList<String> selectedKeywords) {
		ArrayList<Integer> result = new ArrayList<Integer>();

		String[] array = sentence.split(" ");

		for (String keyword : selectedKeywords) {
			for (int i=0; i<array.length; i++) {
				if (keyword.equals(array[i])) {
					result.add(i);
					break;
				}
			}
		}

		return result;
	}

	private String onlyDigitsLettersAndWhiteSpacesAllowedAndToLowerCase(String sentence) {
		String result = "";
		for (int i=0; i<sentence.length(); i++) {
			char c = sentence.charAt(i);
			if (Character.isDigit(c) || Character.isLetter(c) || Character.isWhitespace(c)) {
				c = Character.toLowerCase(c);
				result = result + c;
			}
		}
		return result;
	}

	private String onlyDigitsAndLettersAllowedAndToLowerCase(String sentence) {
		String result = "";
		for (int i=0; i<sentence.length(); i++) {
			char c = sentence.charAt(i);
			if (Character.isDigit(c) || Character.isLetter(c)) {
				c = Character.toLowerCase(c);
				result = result + c;
			}
		}
		return result;
	}
}
