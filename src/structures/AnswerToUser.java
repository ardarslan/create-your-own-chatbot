package structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;

import com.google.common.collect.Sets;

import NLPTools.RootFinder;
import NLPTools.StopWords;
import NLPTools.StringSimilarity;
import databaseTools.Database;
import enums.StateEnum;

public class AnswerToUser {
	String question;
	StateEnum state;
	ArrayList<KeywordsAndAnswer> fullMatches;
	ArrayList<KeywordsAndAnswer> fullMatchesWithTypos;
	HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>> partialMatches;
	HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>> partialMatchesWithTypos;
	HashMap<String, ArrayList<KeywordsAndAnswer>> keywordsAndAnswersContainingWordCache;
	HashMap<String, ArrayList<String>> IDsOfKeywordsAndAnswersContainingWordCache;
	HashMap<String, KeywordsAndAnswer> keywordsAndAnswerCorrespondingToIDCache;
	ArrayList<KeywordAndIDs> allKeywordAndIDsCache;

	public HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>> getPartialMatchesWithTypos() {
		return partialMatchesWithTypos;
	}

	public void setPartialMatchesWithTypos(
			HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>> partialMatchesWithTypos) {
		this.partialMatchesWithTypos = partialMatchesWithTypos;
	}

	public AnswerToUser() {
		fullMatches = new ArrayList<KeywordsAndAnswer>();
		fullMatchesWithTypos = new ArrayList<KeywordsAndAnswer>();
		partialMatches = new HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>>();
		partialMatchesWithTypos = new HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>>();
		keywordsAndAnswersContainingWordCache = new HashMap<String, ArrayList<KeywordsAndAnswer>>();
		IDsOfKeywordsAndAnswersContainingWordCache = new HashMap<String, ArrayList<String>>();
		keywordsAndAnswerCorrespondingToIDCache = new HashMap<String, KeywordsAndAnswer>();
		allKeywordAndIDsCache = new ArrayList<KeywordAndIDs>();
	}

	public HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>> getPartialMatches() {
		return partialMatches;
	}

	public void setPartialMatches(HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>> partialMatches) {
		this.partialMatches = partialMatches;
	}

	public ArrayList<KeywordsAndAnswer> getFullMatchesWithTypos() {
		return fullMatchesWithTypos;
	}

	public void setFullMatchesWithTypos(ArrayList<KeywordsAndAnswer> fullMatchesWithTypos) {
		this.fullMatchesWithTypos = fullMatchesWithTypos;
	}

	public StateEnum getState() {
		return state;
	}

	public void setState(StateEnum state) {
		this.state = state;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public ArrayList<KeywordsAndAnswer> getFullMatches() {
		return fullMatches;
	}

	public void setFullMatches(ArrayList<KeywordsAndAnswer> fullMatches) {
		this.fullMatches = fullMatches;
	}

	public ArrayList<KeywordsAndAnswer> keywordsAndAnswersContainingAllRoots(ArrayList<String> roots) {

		ArrayList<KeywordsAndAnswer> list = new ArrayList<KeywordsAndAnswer>();

		if (roots.size() == 0) {
			return list;
		}
		else if (roots.size() == 1) {
			list = keywordsAndAnswersContainingWord(roots.get(0));
			return list;
		}
		else {
			ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
			ArrayList<String> union = new ArrayList<String>();
			for (String root : roots) {
				ArrayList<String> keywordAndIDsList = IDsOfKeywordsAndAnswersContainingWord(root);
				temp.add(keywordAndIDsList);
				union = unionOfTwoLists(union, keywordAndIDsList);
			}

			for (ArrayList<String> liste : temp) {
				union = intersectionOfTwoLists(union, liste);
			}

			return keywordsAndAnswersCorrespondingToIDs(union);
		}
	}

	public ArrayList<KeywordsAndAnswer> keywordsAndAnswersContainingAllRootsUsingSimilarity(ArrayList<String> roots) {

		ArrayList<KeywordsAndAnswer> list = new ArrayList<KeywordsAndAnswer>();

		if (roots.size() == 0) {
			return list;
		}
		else if (roots.size() == 1) {
			ArrayList<KeywordAndIDs> allKeywordsAndIDs = allKeywordAndIDs();
			ArrayList<ArrayList<KeywordAndIDs>> extendedFullMatchesWithSimilarities = new ArrayList<ArrayList<KeywordAndIDs>>();

			for (int i=0; i<2; i++) {
				extendedFullMatchesWithSimilarities.add(new ArrayList<KeywordAndIDs>());
			}

			StringSimilarity similarity = new StringSimilarity();
			for (KeywordAndIDs keyword : allKeywordsAndIDs) {
				int sim = similarity.execute(roots.get(0), keyword.getKeyword());
				if (sim==0) {
					extendedFullMatchesWithSimilarities.get(0).add(keyword);
				}
				else if (sim==1) {
					extendedFullMatchesWithSimilarities.get(1).add(keyword);
				}
			}

			extendedFullMatchesWithSimilarities.get(0).addAll(extendedFullMatchesWithSimilarities.get(1));

			ArrayList<String> union = new ArrayList<String>();

			for (KeywordAndIDs keywordAndIDs : extendedFullMatchesWithSimilarities.get(0)) {
				union = unionOfTwoLists(union, keywordAndIDs.getIDList());
			}

			list = keywordsAndAnswersCorrespondingToIDs(union);

			return list;
		}
		else {
			ArrayList<KeywordsAndAnswer> intersectionOfListsOfKeywordsAndAnswersWithMatch = new ArrayList<KeywordsAndAnswer>();
			ArrayList<ArrayList<KeywordsAndAnswer>> temp = new ArrayList<ArrayList<KeywordsAndAnswer>>();
			ArrayList<String> listOfRootsWithNoMatch = new ArrayList<String>();
			ArrayList<String> listOfRootsWithMatch = new ArrayList<String>();

			for (int i=0; i<roots.size(); i++) {
				ArrayList<KeywordsAndAnswer> listOfKeywordsAndAnswers = keywordsAndAnswersContainingWord(roots.get(i));
				if (!listOfKeywordsAndAnswers.isEmpty()) {
					listOfRootsWithMatch.add(roots.get(i));
					temp.add(listOfKeywordsAndAnswers);
					intersectionOfListsOfKeywordsAndAnswersWithMatch = unionOfTwoLists(intersectionOfListsOfKeywordsAndAnswersWithMatch, listOfKeywordsAndAnswers);
				}
				else {
					listOfRootsWithNoMatch.add(roots.get(i));
				}
			}

			for (ArrayList<KeywordsAndAnswer> liste : temp) {
				intersectionOfListsOfKeywordsAndAnswersWithMatch = intersectionOfTwoLists(intersectionOfListsOfKeywordsAndAnswersWithMatch, liste);
			}

			if (!listOfRootsWithMatch.isEmpty()) {

				ArrayList<KeywordsAndAnswer> cloneOfIntersectionOfListsOfKeywordsAndAnswersWithMatch = new ArrayList<KeywordsAndAnswer>();
				for (KeywordsAndAnswer keywordsAndAnswer : intersectionOfListsOfKeywordsAndAnswersWithMatch) {
					cloneOfIntersectionOfListsOfKeywordsAndAnswersWithMatch.add(keywordsAndAnswer.deepClone());
				}

				for (KeywordsAndAnswer keywordsAndAnswer : cloneOfIntersectionOfListsOfKeywordsAndAnswersWithMatch) {
					for (Iterator<SynonymsAndQuestion> it = keywordsAndAnswer.getKeywords().iterator(); it.hasNext();) {
						SynonymsAndQuestion aSynonymsAndQuestion = it.next();
						for (String rootWithMatch : listOfRootsWithMatch) {
							if (aSynonymsAndQuestion.getSynonyms().contains(rootWithMatch)) {
								it.remove();
								break;
							}
						}
					}
				}

				StringSimilarity similarity = new StringSimilarity();
				ArrayList<ArrayList<KeywordsAndAnswer>> fullMatchesWithSimilarities = new ArrayList<ArrayList<KeywordsAndAnswer>>();

				for (int i=0; i<20; i++) {
					fullMatchesWithSimilarities.add(new ArrayList<KeywordsAndAnswer>());
				}

				for (int i=0; i<cloneOfIntersectionOfListsOfKeywordsAndAnswersWithMatch.size(); i++) {
					int neededChange=0;
					ArrayList<Integer> locations = new ArrayList<Integer>();
					for (String rootWithNoMatch : listOfRootsWithNoMatch) {
						String[] array = minimumDistanceToKeywordsAndAnswer(rootWithNoMatch, cloneOfIntersectionOfListsOfKeywordsAndAnswersWithMatch.get(i), similarity);
						int newChange = Integer.parseInt(array[0]);
						if (newChange>=0 && newChange<=1) {
							neededChange=neededChange+newChange;
							locations.add(Integer.parseInt(array[1]));
						}
					}
					Set<Integer> set = new HashSet<Integer>(locations);
					if (locations.size()==listOfRootsWithNoMatch.size() && set.size()==locations.size() && neededChange<20) {
						fullMatchesWithSimilarities.get(neededChange).add(intersectionOfListsOfKeywordsAndAnswersWithMatch.get(i));
					}
				}

				for (int t=1; t<fullMatchesWithSimilarities.size(); t++) {
					fullMatchesWithSimilarities.get(0).addAll(fullMatchesWithSimilarities.get(t));
				}
				return fullMatchesWithSimilarities.get(0);
			}
			else {
				ArrayList<KeywordAndIDs> allKeywordsAndIDs = allKeywordAndIDs();
				ArrayList<String> intersectionOfIDs = new ArrayList<String>();
				for (KeywordAndIDs keyword : allKeywordsAndIDs) {
					intersectionOfIDs = unionOfTwoLists(intersectionOfIDs, keyword.getIDList());
				}

				StringSimilarity similarity = new StringSimilarity();
				for (String root : roots) {
					ArrayList<String> liste = new ArrayList<String>();
					for (KeywordAndIDs keyword : allKeywordsAndIDs) {
						int sim = similarity.execute(keyword.getKeyword(), root);
						if (sim>=0 && sim<=1) {
							liste.addAll(keyword.getIDList());
						}
					}
					intersectionOfIDs = intersectionOfTwoLists(intersectionOfIDs, liste);
				}

				ArrayList<KeywordsAndAnswer> listOfkeywordsAndAnswers = keywordsAndAnswersCorrespondingToIDs(intersectionOfIDs);

				ArrayList<ArrayList<KeywordsAndAnswer>> fullMatchesWithSimilarities = new ArrayList<ArrayList<KeywordsAndAnswer>>();

				for (int i=0; i<20; i++) {
					fullMatchesWithSimilarities.add(new ArrayList<KeywordsAndAnswer>());
				}

				for (int i=0; i<listOfkeywordsAndAnswers.size(); i++) {
					int neededChange=0;
					ArrayList<Integer> locations = new ArrayList<Integer>();
					for (String root : roots) {
						String[] array = minimumDistanceToKeywordsAndAnswer(root, listOfkeywordsAndAnswers.get(i), similarity);
						int newChange = Integer.parseInt(array[0]);
						if (newChange>=0 && newChange<=1) {
							neededChange=neededChange+newChange;
							locations.add(Integer.parseInt(array[1]));
						}
					}
					Set<Integer> set = new HashSet<Integer>(locations);
					if (locations.size()==roots.size() && set.size()==locations.size() && neededChange<20) {
						fullMatchesWithSimilarities.get(neededChange).add(listOfkeywordsAndAnswers.get(i));
					}
				}

				for (int t=1; t<fullMatchesWithSimilarities.size(); t++) {
					fullMatchesWithSimilarities.get(0).addAll(fullMatchesWithSimilarities.get(t));
				}

				return fullMatchesWithSimilarities.get(0);
			}
		}
	}

	private String[] minimumDistanceToKeywordsAndAnswer(String word, KeywordsAndAnswer keywordsAndAnswer, StringSimilarity similarity) {
		int min=999;
		int loc=0;
		String closest = "";
		ArrayList<SynonymsAndQuestion> listOfSynonymsAndQuestion = keywordsAndAnswer.getKeywords();
		for (int i=0; i<listOfSynonymsAndQuestion.size(); i++) {
			ArrayList<String> listOfSynonyms = listOfSynonymsAndQuestion.get(i).getSynonyms();
			for (int j=0; j<listOfSynonyms.size(); j++) {
				int sim = similarity.execute(keywordsAndAnswer.getKeywords().get(i).getSynonyms().get(j), word);
				if (sim<min) {
					min=sim;
					loc=i;
					closest=listOfSynonyms.get(j);
				}
			}
		}
		String[] result = new String[3];
		result[0]=String.valueOf(min);
		result[1]=String.valueOf(loc);
		result[2]=closest;
		return result;
	}

	private <T> ArrayList<T> unionOfTwoLists(ArrayList<T> list1, ArrayList<T> list2) {
		Set<T> set = new HashSet<T>();

		set.addAll(list1);
		set.addAll(list2);

		return new ArrayList<T>(set);
	}

	private <T> ArrayList<T> intersectionOfTwoLists(ArrayList<T> list1, ArrayList<T> list2) {
		ArrayList<T> list = new ArrayList<T>();

		for (T t : list1) {
			if(list2.contains(t)) {
				list.add(t);
			}
		}

		return list;
	}
	
	public void getAnswerToUserQuestion(String userQuestion) {
		
		setQuestion(userQuestion);

		ArrayList<String> roots = RootFinder.getInstance().findRootsOfSentence(userQuestion);
		roots = eliminateStopWords(roots);
		ArrayList<ArrayList<String>> listOfRoots = powerSet(roots);

		for (ArrayList<String> list : listOfRoots) {
			ArrayList<KeywordsAndAnswer> fullMatches = keywordsAndAnswersContainingAllRoots(list);

			ArrayList<KeywordsAndAnswer> cloneOfFullMatches = cloneArrayListOfKeywordsAndAnswers(fullMatches);
			ArrayList<KeywordsAndAnswer> cloneOfFullMatchesWithTypos = new ArrayList<KeywordsAndAnswer>();

			ArrayList<KeywordsAndAnswer> fullMatchesWithSizeEqualToListSize = listsWithSize(fullMatches, list.size());

			if (!fullMatchesWithSizeEqualToListSize.isEmpty()) {
				setState(StateEnum.FULL_MATCH);
				setFullMatches(fullMatches);
				return;
			}
			else {
				ArrayList<KeywordsAndAnswer> fullMatchesWithTypos = keywordsAndAnswersContainingAllRootsUsingSimilarity(list);
				cloneOfFullMatchesWithTypos = cloneArrayListOfKeywordsAndAnswers(fullMatchesWithTypos);

				ArrayList<KeywordsAndAnswer> fullMatchesWithTyposWithSizeEqualToListSize = listsWithSize(fullMatchesWithTypos, list.size());

				if (!fullMatchesWithTyposWithSizeEqualToListSize.isEmpty()) {
					setState(StateEnum.FULL_MATCH_WITH_TYPO);
					setFullMatchesWithTypos(fullMatchesWithTypos);
					return;
				}
			}

			ArrayList<KeywordsAndAnswer> partialMatches = cloneOfFullMatches;
			partialMatches = listsWithSize(partialMatches, list.size()+1);

			if (!partialMatches.isEmpty()) {
				HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>> hashMap = groupPartialMatchesAccordingToQuestions(partialMatches, roots);
				setPartialMatches(hashMap);
				setState(StateEnum.PARTIAL_MATCH);
				return;
			}
			else {
				ArrayList<KeywordsAndAnswer> partialMatchesWithTypos = cloneOfFullMatchesWithTypos;
				partialMatchesWithTypos = listsWithSize(partialMatchesWithTypos, list.size()+1);
				if (!partialMatchesWithTypos.isEmpty()) {
					HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>> hashMap = groupPartialMatchesAccordingToQuestionsUsingSimilarity(partialMatchesWithTypos, roots);
					setPartialMatchesWithTypos(hashMap);
					setState(StateEnum.PARTIAL_MATCH_WITH_TYPO);
					return;
				}
			}
		}

		setState(StateEnum.NO_MATCH);
		return;
	}
	
	public AnswerToUser getAnswersToSearch(String userQuestion) {
		
		AnswerToUser answerToUser = new AnswerToUser();
		answerToUser.setQuestion(userQuestion);

		ArrayList<String> roots = RootFinder.getInstance().findRootsOfSentence(userQuestion);
		roots = eliminateStopWords(roots);
		ArrayList<ArrayList<String>> listOfRoots = powerSet(roots);

		for (ArrayList<String> list : listOfRoots) {
			ArrayList<KeywordsAndAnswer> fullMatches = keywordsAndAnswersContainingAllRoots(list);
			ArrayList<KeywordsAndAnswer> fullMatchesWithTypos = keywordsAndAnswersContainingAllRootsUsingSimilarity(list);
			ArrayList<KeywordsAndAnswer> fullMatchesWithSizeEqualToListSize = listsWithSize(fullMatches, list.size());
			ArrayList<KeywordsAndAnswer> fullMatchesWithTyposWithSizeEqualToListSize = listsWithSize(fullMatchesWithTypos, list.size());
			
			ArrayList<KeywordsAndAnswer> partialMatches = listsWithSize(fullMatches, list.size()+1);
			ArrayList<KeywordsAndAnswer> partialMatchesWithTypos = listsWithSize(fullMatchesWithTypos, list.size()+1);
			
			answerToUser.getFullMatches().addAll(fullMatchesWithSizeEqualToListSize);
			answerToUser.getFullMatches().addAll(partialMatches);
			
			answerToUser.getFullMatchesWithTypos().addAll(fullMatchesWithTyposWithSizeEqualToListSize);
			answerToUser.getFullMatchesWithTypos().addAll(partialMatchesWithTypos);
		}
		return answerToUser;
	}

	private Object[] questionOfMissingWord(ArrayList<String> roots, KeywordsAndAnswer keywordsAndAnswer) {
		Object[] result = new Object[2];
		KeywordsAndAnswer clone = keywordsAndAnswer.deepClone();
		for (Iterator<SynonymsAndQuestion> it = clone.getKeywords().iterator(); it.hasNext();) {
			SynonymsAndQuestion aSynonymsAndQuestion = it.next();
			for (String root : roots) {
				if (aSynonymsAndQuestion.getSynonyms().contains(root)) {
					it.remove();
					break;
				}
			}
		}
		result[0]=clone.getKeywords().get(0).getQuestionToUser();
		result[1]=clone.getKeywords().get(0).getSynonyms();
		return result;
	}

	private Object[] questionOfMissingWordUsingSimilarity(ArrayList<String> roots, KeywordsAndAnswer keywordsAndAnswer) {
		StringSimilarity similarity = new StringSimilarity();
		Object[] result = new Object[2];
		KeywordsAndAnswer clone = keywordsAndAnswer.deepClone();
		for (Iterator<SynonymsAndQuestion> it = clone.getKeywords().iterator(); it.hasNext();) {
			SynonymsAndQuestion aSynonymsAndQuestion = it.next();
			boolean quit = false;
			for (String root : roots) {
				if (quit) break;
				for (String s : aSynonymsAndQuestion.getSynonyms()) {
					if (similarity.execute(s, root)<=1) {
						it.remove();
						quit = true;
						break;
					}
				}
			}
		}
		result[0]=clone.getKeywords().get(0).getQuestionToUser();
		result[1]=clone.getKeywords().get(0).getSynonyms();
		return result;
	}
	
	private <T> ArrayList<ArrayList<T>> powerSet(ArrayList<T> elements) {
		Set<T> setOfRoots = new HashSet<T>(elements);
		Set<Set<T>> powerSetOfRoots = Sets.powerSet(setOfRoots);
		ArrayList<ArrayList<T>> listOfRoots = new ArrayList<ArrayList<T>>();

		for (int i=elements.size(); i>0; i--) {
			for (Iterator<Set<T>> it = powerSetOfRoots.iterator(); it.hasNext();) {
				Set<T> aSetString = it.next();
				if (aSetString.size()==i) {
					listOfRoots.add(new ArrayList<T>(aSetString));
					powerSetOfRoots.remove(it);
				}
			}
		}
		return listOfRoots;
	}

	private ArrayList<String> eliminateStopWordsWithDistance(ArrayList<String> roots, int distance, StringSimilarity similarity) {
		List<StopWord> stopWordList = StopWords.getInstance().getStopwords();
		ArrayList<String> deletedRoots = new ArrayList<String>();
		for (String currentRoot : roots) {
			for (StopWord s : stopWordList) {
				String stopWord = s.getStopWord();
				if (similarity.execute(currentRoot, stopWord)<=distance) {
					deletedRoots.add(currentRoot);
					break;
				}
			}
		}
		for (String root : deletedRoots) {
			roots.remove(root);
		}
		return roots;
	}
	
	private ArrayList<String> eliminateStopWords(ArrayList<String> roots) {
		List<StopWord> stopWordList = StopWords.getInstance().getStopwords();
		for (Iterator<String> it = roots.iterator(); it.hasNext(); ) {
		    String root = it.next();
		    if (stopWordList.contains(root)) {
		        it.remove();
		    }
		}
		return roots;
	}

	private ArrayList<KeywordsAndAnswer> cloneArrayListOfKeywordsAndAnswers(ArrayList<KeywordsAndAnswer> list) {
		ArrayList<KeywordsAndAnswer> result = new ArrayList<KeywordsAndAnswer>();
		for (KeywordsAndAnswer t : list) {
			result.add(t.deepClone());
		}
		return result;
	}

	private ArrayList<KeywordsAndAnswer> listsWithSize(ArrayList<KeywordsAndAnswer> list, int size) {
		ArrayList<KeywordsAndAnswer> clone = cloneArrayListOfKeywordsAndAnswers(list);
		if (!clone.isEmpty()) {
			for (Iterator<KeywordsAndAnswer> it = clone.iterator(); it.hasNext();) {
				KeywordsAndAnswer aKeywordsAndAnswer = it.next();
				if (aKeywordsAndAnswer.getKeywords().size()!=size) {
					it.remove();
				}
			}
		}
		return clone;
	}

	private HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>> groupPartialMatchesAccordingToQuestions(ArrayList<KeywordsAndAnswer> list, ArrayList<String> roots) {
		HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>> hashMap = new HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>>();
		for (KeywordsAndAnswer keywordsAndAnswer : list) {
			Object[] objects = questionOfMissingWord(roots, keywordsAndAnswer);
			QuestionToUser questionToUser = (QuestionToUser)objects[0];
			@SuppressWarnings("unchecked")
			ArrayList<String> missingSynonyms = (ArrayList<String>)objects[1];
			PartialKeywordsAndAnswerWithQuestion partialKeywordsAndAnswerWithQuestion = new PartialKeywordsAndAnswerWithQuestion(keywordsAndAnswer, questionToUser, missingSynonyms);
			if (!hashMap.containsKey(questionToUser)) {
				ArrayList<PartialKeywordsAndAnswerWithQuestion> p = new ArrayList<PartialKeywordsAndAnswerWithQuestion>();
				p.add(partialKeywordsAndAnswerWithQuestion);
				hashMap.put(questionToUser, p);
			}
			else {
				hashMap.get(questionToUser).add(partialKeywordsAndAnswerWithQuestion);
			}
		}
		return hashMap;
	}
	
	private HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>> groupPartialMatchesAccordingToQuestionsUsingSimilarity(ArrayList<KeywordsAndAnswer> list, ArrayList<String> roots) {
		HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>> hashMap = new HashMap<QuestionToUser, ArrayList<PartialKeywordsAndAnswerWithQuestion>>();
		for (KeywordsAndAnswer keywordsAndAnswer : list) {
			Object[] objects = questionOfMissingWordUsingSimilarity(roots, keywordsAndAnswer);
			QuestionToUser questionToUser = (QuestionToUser)objects[0];
			@SuppressWarnings("unchecked")
			ArrayList<String> missingSynonyms = (ArrayList<String>)objects[1];
			PartialKeywordsAndAnswerWithQuestion partialKeywordsAndAnswerWithQuestion = new PartialKeywordsAndAnswerWithQuestion(keywordsAndAnswer, questionToUser, missingSynonyms);
			if (!hashMap.containsKey(questionToUser)) {
				ArrayList<PartialKeywordsAndAnswerWithQuestion> p = new ArrayList<PartialKeywordsAndAnswerWithQuestion>();
				p.add(partialKeywordsAndAnswerWithQuestion);
				hashMap.put(questionToUser, p);
			}
			else {
				hashMap.get(questionToUser).add(partialKeywordsAndAnswerWithQuestion);
			}
		}
		return hashMap;
	}

	public ArrayList<KeywordsAndAnswer> keywordsAndAnswersContainingWord(String word) {
		if (!keywordsAndAnswersContainingWordCache.containsKey(word)) {
			ArrayList<KeywordsAndAnswer> result = new ArrayList<KeywordsAndAnswer>();
			final Query<KeywordAndIDs> query = Database.getInstance().getKeywordsCollection().createQuery(KeywordAndIDs.class);
			Query<KeywordAndIDs> keywordAndIDsQuery = query.filter("keyword =", word);
			List<KeywordAndIDs> keywordAndIDsList = keywordAndIDsQuery.asList(new FindOptions().limit(1));
			if (keywordAndIDsList != null && keywordAndIDsList.size()!=0) {
				for (String ID : keywordAndIDsList.get(0).getIDList()) {
					final Query<KeywordsAndAnswer> newQuery = Database.getInstance().getKeywordsAndAnswerCollection().createQuery(KeywordsAndAnswer.class);
					Query<KeywordsAndAnswer> keywordsAndAnswersQuery = newQuery.filter("_id =", new ObjectId(ID));
					List<KeywordsAndAnswer> keywordsAndAnswersList = keywordsAndAnswersQuery.asList(new FindOptions().limit(1));
					result.add(keywordsAndAnswersList.get(0));
				}
			}
			keywordsAndAnswersContainingWordCache.put(word, result);
			return result;
		}
		else {
			return keywordsAndAnswersContainingWordCache.get(word);
		}
	}

	public ArrayList<String> IDsOfKeywordsAndAnswersContainingWord(String word) {

		if (!IDsOfKeywordsAndAnswersContainingWordCache.containsKey(word)) {
			final Query<KeywordAndIDs> query = Database.getInstance().getKeywordsCollection().createQuery(KeywordAndIDs.class);
			Query<KeywordAndIDs> keywordAndIDsQuery = query.filter("keyword =", word);
			List<KeywordAndIDs> keywordAndIDsList = keywordAndIDsQuery.asList(new FindOptions().limit(1));

			ArrayList<String> result;

			if (keywordAndIDsList!=null && keywordAndIDsList.size()!=0) {
				result=keywordAndIDsList.get(0).getIDList();
			}
			else {
				result=new ArrayList<String>();
			}
			IDsOfKeywordsAndAnswersContainingWordCache.put(word,result);
			return result;
		}
		else {
			return IDsOfKeywordsAndAnswersContainingWordCache.get(word);
		}
	}

	public ArrayList<KeywordsAndAnswer> keywordsAndAnswersCorrespondingToIDs(ArrayList<String> IDs) {
		ArrayList<KeywordsAndAnswer> result = new ArrayList<KeywordsAndAnswer>();
		for (String ID : IDs) {
			result.add(keywordsAndAnswerCorrespondingToID(ID));
		}
		return result;
	}

	public KeywordsAndAnswer keywordsAndAnswerCorrespondingToID(String ID) {
		if (!keywordsAndAnswerCorrespondingToIDCache.containsKey(ID)) { 
			final Query<KeywordsAndAnswer> newQuery = Database.getInstance().getKeywordsAndAnswerCollection().createQuery(KeywordsAndAnswer.class);
			Query<KeywordsAndAnswer> keywordsAndAnswersQuery = newQuery.filter("_id =", new ObjectId(ID));
			List<KeywordsAndAnswer> keywordsAndAnswersList = keywordsAndAnswersQuery.asList(new FindOptions().limit(1));
			KeywordsAndAnswer result = keywordsAndAnswersList.get(0); 

			keywordsAndAnswerCorrespondingToIDCache.put(ID, result);
			return result;
		}
		else {
			return keywordsAndAnswerCorrespondingToIDCache.get(ID);
		}
	}

	public ArrayList<KeywordAndIDs> allKeywordAndIDs() {
		if (allKeywordAndIDsCache.isEmpty()) {
			final Query<KeywordAndIDs> query = Database.getInstance().getKeywordsCollection().createQuery(KeywordAndIDs.class);
			allKeywordAndIDsCache = (ArrayList<KeywordAndIDs>)query.asList();
			return (ArrayList<KeywordAndIDs>)query.asList();
		}
		else {
			return allKeywordAndIDsCache;
		}
	}

}
