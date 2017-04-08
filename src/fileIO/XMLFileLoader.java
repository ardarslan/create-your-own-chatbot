package fileIO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import controllers.CreateNewKeywordsAndAnswerController;
import structures.Entity;
import structures.KeywordsAndAnswer;

public class XMLFileLoader implements FileLoader {
	CreateNewKeywordsAndAnswerController createNewKeywordsAndAnswerController = new CreateNewKeywordsAndAnswerController();
	public void loadFile(File file) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();
			NodeList questionsAndAnswersList = doc.getElementsByTagName("questionAndAnswer");
			for (int i = 0; i < questionsAndAnswersList.getLength(); i++) {
				Node nNode = questionsAndAnswersList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String question = eElement.getAttribute("question");
					String answer = eElement.getAttribute("answer");
					String stringOfSelectedKeywords = eElement.getAttribute("selectedKeywords");
					if (stringOfSelectedKeywords == null || stringOfSelectedKeywords.equals("")) {
						String[] rootsOfQuestionArray = question.split(" ");
						ArrayList<String> rootsOfQuestionList = new ArrayList<String>();
						for (String word : rootsOfQuestionArray) {
							rootsOfQuestionList.add(word);
						}
						KeywordsAndAnswer keywordsAndAnswer = createNewKeywordsAndAnswerController.createNewKeywordsAndAnswer(question, rootsOfQuestionList, answer);
						createNewKeywordsAndAnswerController.insertKeywordsAndAnswerToDatabase(keywordsAndAnswer);
					}
					else {
						String[] rootsOfSelectedKeywordsArray = stringOfSelectedKeywords.split(" ");
						ArrayList<String> rootsOfSelectedKeywordsList =  new ArrayList<String>();
						for (String word : rootsOfSelectedKeywordsArray) {
							rootsOfSelectedKeywordsList.add(word);
						}
						KeywordsAndAnswer keywordsAndAnswer = createNewKeywordsAndAnswerController.createNewKeywordsAndAnswer(question, rootsOfSelectedKeywordsList, answer);
						createNewKeywordsAndAnswerController.insertKeywordsAndAnswerToDatabase(keywordsAndAnswer);
					}
				}
			}

		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
