

import zemberek.core.turkish.PrimaryPos;
import zemberek.core.turkish.SecondaryPos;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.morphology.analysis.tr.TurkishMorphology;
import zemberek.morphology.lexicon.DictionaryItem;

import java.io.IOException;
import java.util.List;


public class AddNewDictionaryItem {

    public static void main(String[] args) throws IOException {
        TurkishMorphology parserGenerator = TurkishMorphology.createWithDefaults();
        String input = "tweetleyeyazdım";
        List<WordAnalysis> before = parserGenerator.analyze(input);
        System.out.println("Parses for " + input + " before adding lemma `tweetleyeyazdım` = " + before);
        DictionaryItem item =
                new DictionaryItem("tweetlemek", "tweetle", "tivitle", PrimaryPos.Verb, SecondaryPos.None);
        parserGenerator.getGraph().addDictionaryItem(item);
        parserGenerator.invalidateAllCache();
        List<WordAnalysis> after = parserGenerator.analyze(input);
        System.out.println("Parses for " + input + " after adding lemma `tweetleyeyazdım` = " + after);
    }
}