package structures;

import java.util.ArrayList;

import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

public class Synonym
    {
        @Id
        @Property("id")
        private String word;
        private ArrayList<String> synonyms;
        
        public Synonym() {
        	
        }

        public Synonym(String word, ArrayList<String> synonyms)
        {
            this.word = word;
            this.synonyms = synonyms;
        }

		public String getWord() {
			return word;
		}

		public void setWord(String word) {
			this.word = word;
		}

		public ArrayList<String> getSynonyms() {
			return synonyms;
		}

		public void setSynonyms(ArrayList<String> synonyms) {
			this.synonyms = synonyms;
		}
    }
