package databaseTools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.mongodb.MongoClient;

import structures.Entity;
import structures.EntityForDatabase;
import structures.Form;
import structures.KeywordAndIDs;
import structures.KeywordsAndAnswer;
import structures.StopWord;
import structures.Synonym;
import structures.SynonymsAndQuestion;

public class Database
{
	public MongoClient mongoClient;
	private static Database instance;
	private Morphia morphia;
	private Datastore keywordsAndAnswerCollection;
	private Datastore keywordsCollection;
	private Datastore synonymsCollection;
	private Datastore stopwordsCollection;
	private Datastore emptyFormsCollection;
	private Datastore fullFormsCollection;
	private Datastore entitiesCollection;

	private Database()
	{
		mongoClient = new MongoClient();
		morphia = new Morphia();

		keywordsAndAnswerCollection = morphia.createDatastore(mongoClient, "keywordsAndAnswer");
		keywordsCollection = morphia.createDatastore(mongoClient, "keywords");
		synonymsCollection = morphia.createDatastore(mongoClient, "synonyms");
		stopwordsCollection = morphia.createDatastore(mongoClient, "stopwords");
		emptyFormsCollection = morphia.createDatastore(mongoClient, "emptyForms");
		fullFormsCollection = morphia.createDatastore(mongoClient, "fullForms");
		entitiesCollection = morphia.createDatastore(mongoClient, "entities");

		keywordsAndAnswerCollection.ensureIndexes();
		keywordsCollection.ensureIndexes();
		synonymsCollection.ensureIndexes();
		stopwordsCollection.ensureIndexes();
		emptyFormsCollection.ensureIndexes();
		fullFormsCollection.ensureIndexes();
		entitiesCollection.ensureIndexes();
	}

	public static Database getInstance()
	{
		Object lock = new Object();
		if (instance == null)
		{
			synchronized (lock)
			{
				if (instance == null)
				{
					instance = new Database();
				}
			}
		}
		return instance;
	}

	public Datastore getKeywordsAndAnswerCollection()
	{
		return keywordsAndAnswerCollection;
	}

	public Datastore getKeywordsCollection()
	{
		return keywordsCollection;
	}

	public Datastore getSynonymsCollection()
	{
		return synonymsCollection;
	}

	public Datastore getStopwordsCollection() {
		return stopwordsCollection;
	}
	
	public Datastore getEmptyFormsCollection() {
		return emptyFormsCollection;
	}
	
	public Datastore getFullFormsCollection() {
		return fullFormsCollection;
	}
	
	public Datastore getEntitiesCollection() {
		return entitiesCollection;
	}

	public ArrayList<KeywordsAndAnswer> keywordsAndAnswersContainingWord(String word) {
		ArrayList<KeywordsAndAnswer> result = new ArrayList<KeywordsAndAnswer>();
		final Query<KeywordAndIDs> query = keywordsCollection.createQuery(KeywordAndIDs.class);
		Query<KeywordAndIDs> keywordAndIDsQuery = query.filter("keyword =", word);
		List<KeywordAndIDs> keywordAndIDsList = keywordAndIDsQuery.asList(new FindOptions().limit(1));
		if (keywordAndIDsList != null && keywordAndIDsList.size()!=0) {
			for (String ID : keywordAndIDsList.get(0).getIDList()) {
				final Query<KeywordsAndAnswer> newQuery = keywordsAndAnswerCollection.createQuery(KeywordsAndAnswer.class);
				Query<KeywordsAndAnswer> keywordsAndAnswersQuery = newQuery.filter("_id =", new ObjectId(ID));
				List<KeywordsAndAnswer> keywordsAndAnswersList = keywordsAndAnswersQuery.asList(new FindOptions().limit(1));
				result.add(keywordsAndAnswersList.get(0));
			}
		}
		return result;
	}

	public ArrayList<String> IDsOfKeywordsAndAnswersContainingWord(String word) {
		final Query<KeywordAndIDs> query = keywordsCollection.createQuery(KeywordAndIDs.class);
		Query<KeywordAndIDs> keywordAndIDsQuery = query.filter("keyword =", word);
		List<KeywordAndIDs> keywordAndIDsList = keywordAndIDsQuery.asList(new FindOptions().limit(1));
		if (keywordAndIDsList!=null && keywordAndIDsList.size()!=0) {
			return keywordAndIDsList.get(0).getIDList();
		}
		else {
			return new ArrayList<String>();
		}
	}

	public ArrayList<KeywordsAndAnswer> keywordsAndAnswersCorrespondingToIDs(ArrayList<String> IDs) {
		ArrayList<KeywordsAndAnswer> result = new ArrayList<KeywordsAndAnswer>();
		for (String ID : IDs) {
			final Query<KeywordsAndAnswer> newQuery = keywordsAndAnswerCollection.createQuery(KeywordsAndAnswer.class);
			Query<KeywordsAndAnswer> keywordsAndAnswersQuery = newQuery.filter("_id =", new ObjectId(ID));
			List<KeywordsAndAnswer> keywordsAndAnswersList = keywordsAndAnswersQuery.asList(new FindOptions().limit(1));
			result.add(keywordsAndAnswersList.get(0));
		}
		return result;
	}
	
	public Form FormCorrespondingToID(String ID) {
		final Query<Form> newQuery = emptyFormsCollection.createQuery(Form.class);
		Query<Form> formsQuery = newQuery.filter("_id =", new ObjectId(ID));
		List<Form> formsList = formsQuery.asList(new FindOptions().limit(1));
		formsList.get(0).setAnswers(new ArrayList<String>());
		return formsList.get(0);
	}
 
	public synchronized void insertKeywordsAndAnswerToDatabase(KeywordsAndAnswer keywordsAndAnswer)
	{
		keywordsAndAnswerCollection.save(keywordsAndAnswer);
		String ID = keywordsAndAnswer.get_id().toString();

		for (SynonymsAndQuestion synonymsAndQuestion : keywordsAndAnswer.getKeywords())
		{
			for (String word : synonymsAndQuestion.getSynonyms())
			{
				final Query<KeywordAndIDs> query = keywordsCollection.createQuery(KeywordAndIDs.class);
				Query<KeywordAndIDs> keywordAndIDsQuery = query.filter("keyword =", word);
				List<KeywordAndIDs> keywordAndIDs = keywordAndIDsQuery.asList(new FindOptions().limit(1));
				KeywordAndIDs newKeywordAndIDs;
				if (keywordAndIDs.isEmpty())
				{
					ArrayList<String> arrayList = new ArrayList<String>();
					arrayList.add(ID);
					newKeywordAndIDs = new KeywordAndIDs(word, arrayList);
					keywordsCollection.save(newKeywordAndIDs);
				}
				else
				{
					keywordAndIDs.get(0).getIDList().add(ID);
					UpdateOperations<KeywordAndIDs> updateOperations = keywordsCollection.createUpdateOperations(KeywordAndIDs.class)
							.set("IDList", keywordAndIDs.get(0).getIDList());
					keywordsCollection.update(keywordAndIDsQuery, updateOperations);
				}
			}
		}
	}
	
	public synchronized void insertEmptyFormToDatabase(Form emptyForm, KeywordsAndAnswer keywordsAndAnswer)
	{
		emptyFormsCollection.save(emptyForm);
		String formID = emptyForm.get_id().toString();
		keywordsAndAnswer.setAnswer("&Form"+formID);
		keywordsAndAnswerCollection.save(keywordsAndAnswer);
		String ID = keywordsAndAnswer.get_id().toString();

		for (SynonymsAndQuestion synonymsAndQuestion : keywordsAndAnswer.getKeywords())
		{
			for (String word : synonymsAndQuestion.getSynonyms())
			{
				final Query<KeywordAndIDs> query = keywordsCollection.createQuery(KeywordAndIDs.class);
				Query<KeywordAndIDs> keywordAndIDsQuery = query.filter("keyword =", word);
				List<KeywordAndIDs> keywordAndIDs = keywordAndIDsQuery.asList(new FindOptions().limit(1));
				KeywordAndIDs newKeywordAndIDs;
				if (keywordAndIDs.isEmpty())
				{
					ArrayList<String> arrayList = new ArrayList<String>();
					arrayList.add(ID);
					newKeywordAndIDs = new KeywordAndIDs(word, arrayList);
					keywordsCollection.save(newKeywordAndIDs);
				}
				else
				{
					keywordAndIDs.get(0).getIDList().add(ID);
					UpdateOperations<KeywordAndIDs> updateOperations = keywordsCollection.createUpdateOperations(KeywordAndIDs.class)
							.set("IDList", keywordAndIDs.get(0).getIDList());
					keywordsCollection.update(keywordAndIDsQuery, updateOperations);
				}
			}
		}
	}
	
	public synchronized void insertFullFormToDatabase(Form fullForm) {
		fullFormsCollection.save(fullForm);
	}

	public synchronized void insertSynonymToDatabase(String firstWord, String secondWord)
	{
		firstWord = firstWord.toLowerCase();
		secondWord = secondWord.toLowerCase();

		if (firstWord.equals(secondWord))
		{
			return;
		}

		final Query<Synonym> query1 = synonymsCollection.createQuery(Synonym.class);
		Query<Synonym> synonymsQuery1 = query1.filter("word =", firstWord);
		List<Synonym> synonymsList1 = synonymsQuery1.asList();

		final Query<Synonym> query2 = synonymsCollection.createQuery(Synonym.class);
		Query<Synonym> synonymsQuery2 = query2.filter("word =", secondWord);
		List<Synonym> synonymsList2 = synonymsQuery2.asList();

		if (synonymsList1.size() == 0 && synonymsList2.size() == 0)
		{
			Synonym synonym1 = new Synonym(firstWord, new ArrayList<String>());
			synonym1.getSynonyms().add(secondWord);
			Synonym synonym2 = new Synonym(secondWord, new ArrayList<String>());
			synonym2.getSynonyms().add(firstWord);
			synonymsCollection.save(synonym1);
			synonymsCollection.save(synonym2);
		}
		else if (synonymsList1.size() == 0 && synonymsList2.size() != 0)
		{
			Synonym synonym1 = new Synonym(firstWord, new ArrayList<String>());
			synonym1.getSynonyms().add(secondWord);
			synonymsCollection.save(synonym1);
			if (!synonymsList2.get(0).getSynonyms().contains(firstWord))
			{
				synonymsList2.get(0).getSynonyms().add(firstWord);
				UpdateOperations<Synonym> updateOperations = synonymsCollection.createUpdateOperations(Synonym.class)
						.set("synonyms", synonymsList2.get(0).getSynonyms());
				synonymsCollection.update(synonymsQuery2, updateOperations);
			}
		}
		else if (synonymsList1.size() != 0 && synonymsList2.size() == 0)
		{
			Synonym synonym2 = new Synonym(secondWord, new ArrayList<String>());
			synonym2.getSynonyms().add(firstWord);
			synonymsCollection.save(synonym2);
			if (!synonymsList1.get(0).getSynonyms().contains(secondWord))
			{
				synonymsList1.get(0).getSynonyms().add(secondWord);
				UpdateOperations<Synonym> updateOperations = synonymsCollection.createUpdateOperations(Synonym.class)
						.set("synonyms", synonymsList1.get(0).getSynonyms());
				synonymsCollection.update(synonymsQuery1, updateOperations);
			}
		}
		else
		{
			if (!synonymsList2.get(0).getSynonyms().contains(firstWord))
			{
				synonymsList2.get(0).getSynonyms().add(firstWord);
				UpdateOperations<Synonym> updateOperations = synonymsCollection.createUpdateOperations(Synonym.class)
						.set("synonyms", synonymsList2.get(0).getSynonyms());
				synonymsCollection.update(synonymsQuery2, updateOperations);
			}
			if (!synonymsList1.get(0).getSynonyms().contains(secondWord))
			{
				synonymsList1.get(0).getSynonyms().add(secondWord);
				UpdateOperations<Synonym> updateOperations = synonymsCollection.createUpdateOperations(Synonym.class)
						.set("synonyms", synonymsList1.get(0).getSynonyms());
				synonymsCollection.update(synonymsQuery1, updateOperations);
			}
		}
	}

	public synchronized void insertStopwordToDatabase(String stopWord) {
		final Query<StopWord> query = stopwordsCollection.createQuery(StopWord.class);
		Query<StopWord> stopWordsQuery = query.filter("stopWord =", stopWord);
		List<StopWord> stopWordsList = stopWordsQuery.asList();
		if (stopWordsList.isEmpty()) stopwordsCollection.save(new StopWord(stopWord));
	}
	
	public synchronized void insertEntityToDatabase(Entity entity) {
		final Query<EntityForDatabase> query = entitiesCollection.createQuery(EntityForDatabase.class);
		Query<EntityForDatabase> entitiesQuery = query.filter("type =", entity.getType());
		List<EntityForDatabase> entitiesList = entitiesQuery.asList();
		if (entitiesList.isEmpty()) {
			ArrayList<String> values = new ArrayList<String>();
			values.add(entity.getValue());
			entitiesCollection.save(new EntityForDatabase(entity.getType(), values));
		}
		else {
			entitiesList.get(0).getValues().add(entity.getValue());
			UpdateOperations<EntityForDatabase> updateOperations = entitiesCollection.createUpdateOperations(EntityForDatabase.class)
					.set("values", entitiesList.get(0).getValues());
			synonymsCollection.update(entitiesQuery, updateOperations);
		}
	}

	public ArrayList<String> getAllSynonymsOfAWordFromDatabase(String word) {
		final Query<Synonym> query = synonymsCollection.createQuery(Synonym.class);
		Query<Synonym> synonymsQuery = query.filter("word =", word);
		List<Synonym> synonymsList = synonymsQuery.asList(new FindOptions().limit(1));
		if (synonymsList.isEmpty()) return new ArrayList<String>();
		ArrayList<String> synonymsOfWord = synonymsList.get(0).getSynonyms();
		ArrayList<String> result = new ArrayList<String>();
		if (synonymsOfWord.size()==1) {
			result.add(synonymsOfWord.get(0));
		}
		else if (synonymsOfWord.size()==2) {
			result.add(synonymsOfWord.get(0));
			result.add(synonymsOfWord.get(1));
		}
		else {
			result.add(synonymsOfWord.get(0));
			result.add(synonymsOfWord.get(1));
			result.add(synonymsOfWord.get(2));
		}
		return result;
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

	public String getExistingQuestionForMissingRootFromDatabase(String root, ArrayList<String> rootsOfSelectedKeywords) {
		ArrayList<String> editedRootsOfSelectedKeywords = new ArrayList<String>();
		for (String word : rootsOfSelectedKeywords) {
			if (!word.equals(root)) {
				editedRootsOfSelectedKeywords.add(word);
			}
		}

		ArrayList<KeywordsAndAnswer> keywordsAndAnswersContainingAllRoots = keywordsAndAnswersContainingAllRoots(editedRootsOfSelectedKeywords);

		for (KeywordsAndAnswer keywordsAndAnswer : keywordsAndAnswersContainingAllRoots) {
			if (keywordsAndAnswer.getKeywords().size()==rootsOfSelectedKeywords.size()) {
				for (SynonymsAndQuestion synonymsAndQuestion : keywordsAndAnswer.getKeywords()) {
					if (!synonymsAndQuestion.getQuestionToUser().isYesNo()
							&& intersectionOfTwoLists(synonymsAndQuestion.getSynonyms(), editedRootsOfSelectedKeywords).size()==0) {
						return synonymsAndQuestion.getQuestionToUser().getQuestion();
					}
				}
			}
		}
		return null;
	}

	public ArrayList<String> getExistingSynonymsForAWordFromDatabase(String root, ArrayList<String> rootsOfSelectedKeywords) {
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<ArrayList<KeywordsAndAnswer>> list = new ArrayList<ArrayList<KeywordsAndAnswer>>();
		KeywordsAndAnswer found = null;
		for (int i=0; i<rootsOfSelectedKeywords.size(); i++) {
			list.add(new ArrayList<KeywordsAndAnswer>());
		}

		ArrayList<String> IDs = IDsOfKeywordsAndAnswersContainingWord(root);
		ArrayList<KeywordsAndAnswer> keywordsAndAnswers = keywordsAndAnswersCorrespondingToIDs(IDs);
		for (KeywordsAndAnswer keywordsAndAnswer : keywordsAndAnswers) {
			int similarWords = numberOfSimilarWordsBetweenTwoKeywordsAndAnswers(rootsOfSelectedKeywords, keywordsAndAnswer);
			list.get(similarWords-1).add(keywordsAndAnswer);
		}
		for (int i=rootsOfSelectedKeywords.size()-1; i>=0; i--) {
			for (int j=0; j<list.get(i).size(); j++) {
				KeywordsAndAnswer temp = list.get(i).get(j);
				if (temp!=null) {
					found = temp;
				}
			}
		}
		if (found==null) return result;
		for (SynonymsAndQuestion synonymsAndQuestion : found.getKeywords()) {
			if (synonymsAndQuestion.getSynonyms().contains(root)) {
				result = synonymsAndQuestion.getSynonyms();
				break;
			}
		}
		result.remove(root);
		return result;
	}

	private int numberOfSimilarWordsBetweenTwoKeywordsAndAnswers(ArrayList<String> rootsOfSelectedKeywords, KeywordsAndAnswer keywordsAndAnswer) {
		int count=0;
		for (SynonymsAndQuestion synonymsAndQuestion : keywordsAndAnswer.getKeywords()) {
			boolean exit=false;
			for (String synonym : synonymsAndQuestion.getSynonyms()) {
				if (exit) break;
				for (String root : rootsOfSelectedKeywords) {
					if (root.equals(synonym)) {
						count++;
						exit=true;
						break;
					}
				}
			}
		}
		return count;
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
}