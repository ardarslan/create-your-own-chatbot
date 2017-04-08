package NLPTools;

import zemberek.morphology.ambiguity.Z3MarkovModelDisambiguator;
import zemberek.morphology.analysis.SentenceAnalysis;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.morphology.analysis.tr.TurkishMorphology;
import zemberek.morphology.analysis.tr.TurkishSentenceAnalyzer;

import java.io.IOException;
import java.util.ArrayList;

import net.zemberek.erisim.Zemberek;
import net.zemberek.tr.yapi.TurkiyeTurkcesi;

public class RootFinder {

	private static TurkishSentenceAnalyzer sentenceAnalyzer;
	private Zemberek zemberek;
	
	private RootFinder() {
		zemberek = new Zemberek(new TurkiyeTurkcesi());
		TurkishMorphology morphology;
		try {
			morphology = TurkishMorphology.createWithDefaults();
			Z3MarkovModelDisambiguator disambiguator = new Z3MarkovModelDisambiguator();
			sentenceAnalyzer = new TurkishSentenceAnalyzer(morphology, disambiguator);
			
//			 String input = "tweetleyeyazdım";
//		        List<WordAnalysis> before = morphology.analyze(input);
//		        System.out.println("Parses for " + input + " before adding lemma `tweetleyeyazdım` = " + before);
//		        DictionaryItem item =
//		                new DictionaryItem("tweetlemek", "tweetle", "tivitle", PrimaryPos.Verb, SecondaryPos.None);
//		        morphology.getGraph().addDictionaryItem(item);
//		        morphology.invalidateAllCache();
//		        List<WordAnalysis> after = morphology.analyze(input);
//		        System.out.println("Parses for " + input + " after adding lemma `tweetleyeyazdım` = " + after);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static RootFinder instance;

	public static RootFinder getInstance() {
		if (instance == null) {
			instance = new RootFinder();
		}
		return instance;
	}

	public ArrayList<String> findRootsOfSentence(String sentence) {
		SentenceAnalysis result = sentenceAnalyzer.analyze(sentence);
		sentenceAnalyzer.disambiguate(result);
		ArrayList<String> list = new ArrayList<String>();
		for (SentenceAnalysis.Entry entry : result) {
			WordAnalysis beforeLemmatization = entry.parses.get(0);
			String root = beforeLemmatization.getLemma();
			if (root.equals("UNK")) {
				String[] suggestions = zemberek.oner(beforeLemmatization.root);
				if (suggestions.length != 0) {
					String temp = findRootOfWord(suggestions[0]);
					list.add(temp);
				}
				else {
					list.add(beforeLemmatization.root);
				}
			}
			else {
				if (Character.isDigit(root.charAt(0)) || Character.isLetter(root.charAt(0))) {
					list.add(root.toLowerCase());
				}
			}
		}
		return list;
	}

	public String findRootOfWord(String word) {
		SentenceAnalysis result = sentenceAnalyzer.analyze(word);
		sentenceAnalyzer.disambiguate(result);
		WordAnalysis beforeLemmatization = result.iterator().next().parses.get(0);
		String root = beforeLemmatization.getLemma();
		if (root.equals("UNK")) {
			String[] suggestions = zemberek.oner(beforeLemmatization.root);
			if (suggestions.length != 0) {
				root = findRootOfWord(suggestions[0]);
			}
			else {
				root = beforeLemmatization.root;
			}
		}
		return root.toLowerCase();
	}
}
