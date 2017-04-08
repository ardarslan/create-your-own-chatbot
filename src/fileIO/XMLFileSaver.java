package fileIO;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.mongodb.morphia.query.Query;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import databaseTools.Database;
import structures.KeywordsAndAnswer;
import structures.SynonymsAndQuestion;

public class XMLFileSaver implements FileSaver {
	
	public XMLFileSaver() {
		
	}
	
	public void saveFile(String path) {
		final Query<KeywordsAndAnswer> query = Database.getInstance().getKeywordsAndAnswerCollection().createQuery(KeywordsAndAnswer.class);
    	List<KeywordsAndAnswer> keywordsAndAnswersList = query.asList();
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("stories");
			doc.appendChild(rootElement);
			
			Element questionsAndAnswersElement = doc.createElement("questionsAndAnswers");
			rootElement.appendChild(questionsAndAnswersElement);
			
			for (KeywordsAndAnswer keywordsAndAnswer : keywordsAndAnswersList) {
			
			Element questionAndAnswerElement = doc.createElement("questionAndAnswer");
			questionsAndAnswersElement.appendChild(questionAndAnswerElement);

			Attr attrQuestion = doc.createAttribute("question");
			attrQuestion.setValue(keywordsAndAnswer.getQuestion());
			questionAndAnswerElement.setAttributeNode(attrQuestion);
			
			Attr attrSelectedKeywords = doc.createAttribute("selectedKeywords");
			String selected = "";
			for (SynonymsAndQuestion synonymsAndQuestion : keywordsAndAnswer.getKeywords()) {
				String addition = synonymsAndQuestion.getSynonyms().get(0);
				if (addition != null && addition != "" && selected!=null && selected!="") {
					selected = selected+", "+addition;
				}
				else if (addition != null && addition != "" && selected!=null && selected=="") {
					selected = addition;
				}
			}
			attrSelectedKeywords.setValue(selected);
			questionAndAnswerElement.setAttributeNode(attrSelectedKeywords);
			
			Attr attrAnswer = doc.createAttribute("answer");
			attrAnswer.setValue(keywordsAndAnswer.getAnswer());
			questionAndAnswerElement.setAttributeNode(attrAnswer);
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result;
			if (path.substring(path.length()-4).equals(".xml")) {
				result = new StreamResult(new File(path));
			}
			else {
				result = new StreamResult(new File(path+".xml"));
			}
			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
	
}
