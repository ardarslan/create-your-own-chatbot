package GUI;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

import NLPTools.RootFinder;
import controllers.CreateNewKeywordsAndAnswerController;
import net.miginfocom.swing.MigLayout;
import structures.KeywordsAndAnswer;
import structures.SynonymsAndQuestion;

public class CreateNewKeywordsAndAnswerPanel implements KeyListener
{   
	private CreateNewKeywordsAndAnswerController createNewKeywordsAndAnswerController;

	private JFrame frame;
	private JPanel mainQuestionPanel;
	private JPanel keywordsPanel;
	private JPanel answerPanel;
	private JTextArea answer;

	private JButton generatePossibleKeywordsButton;

	private JTextField question;
	private JButton insertButton;
	private KeywordsAndAnswer keywordsAndAnswer;
	private ArrayList<JTextField> subQuestions;
	private ArrayList<ArrayList<JTextField>> synonymsList;
	private ArrayList<JCheckBox> checkBoxes;
	private ArrayList<JPanel> keywordPanels;

	public CreateNewKeywordsAndAnswerPanel()
	{
		synonymsList = new ArrayList<ArrayList<JTextField>>();
		subQuestions = new ArrayList<JTextField>();
		checkBoxes = new ArrayList<JCheckBox>();
		
		createNewKeywordsAndAnswerController = new CreateNewKeywordsAndAnswerController();
		mainQuestionPanel = new JPanel();

		keywordsPanel = new JPanel();
		keywordsPanel.setLayout(new FlowLayout());
		answerPanel = new JPanel();
		keywordPanels = new ArrayList<JPanel>();

		question = new JTextField(60);
		question.setCaretPosition(0);
		frame = new JFrame("Create New Entry");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		generatePossibleKeywordsButton = new JButton("Generate");
		insertButton = new JButton("Insert to Database");
		question.addKeyListener(this);
		
		insertButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				keywordsAndAnswer.setAnswer(answer.getText());
				ArrayList<SynonymsAndQuestion> synonymsAndQuestionList = new ArrayList<SynonymsAndQuestion>();
				for (int i=0; i<checkBoxes.size(); i++) {
					JCheckBox checkBox = checkBoxes.get(i);
					if (checkBox.isSelected()) {
					ArrayList<String> keywords = new ArrayList<String>();
					keywords.add(checkBox.getText());
					for (int j=0; j<synonymsList.get(i).size(); j++) {
						String temp = synonymsList.get(i).get(j).getText();
						if (!temp.equals("") && temp!=null)
						keywords.add(temp);
					}
					String question = subQuestions.get(i).getText();
					synonymsAndQuestionList.add(new SynonymsAndQuestion(keywords, question, true));
					}
				}
				keywordsAndAnswer.setKeywords(synonymsAndQuestionList);
				keywordsAndAnswer.setQuestion(question.getText());
				createNewKeywordsAndAnswerController.insertKeywordsAndAnswerToDatabase(keywordsAndAnswer);
				JOptionPane.showMessageDialog(new JPanel(), "Başarıyla kaydedildi.");
			}
		});

		generatePossibleKeywordsButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				generateQuestionsAndSynonyms();
			}
		});

		mainQuestionPanel.add(question);
		mainQuestionPanel.add(generatePossibleKeywordsButton);

		frame.add(mainQuestionPanel);
		frame.add(keywordsPanel);	
		frame.add(answerPanel);

		frame.pack();
		frame.setSize(850,650);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void generateQuestionsAndSynonyms() {

		for (JPanel panel : keywordPanels) {
			panel.setVisible(false);
			frame.remove(panel);
		}
		
		synonymsList = new ArrayList<ArrayList<JTextField>>();
		subQuestions = new ArrayList<JTextField>();
		checkBoxes = new ArrayList<JCheckBox>();
		
		
		
		keywordsAndAnswer = createNewKeywordsAndAnswerController.createNewKeywordsAndAnswer(question.getText(), new ArrayList<String>(), "");

		int lengthOfTextField = 60/keywordsAndAnswer.getKeywords().size();
		ArrayList<SynonymsAndQuestion> synonymsAndQuestionList = keywordsAndAnswer.getKeywords();
		for (int i=0; i<synonymsAndQuestionList.size(); i++) {
			final int index = i;
			synonymsList.add(new ArrayList<JTextField>());
			SynonymsAndQuestion synonymsAndQuestion = synonymsAndQuestionList.get(i);
			JPanel keywordPanel = new JPanel();
			keywordPanels.add(keywordPanel);
			JTextField subQuestion = new JTextField(lengthOfTextField);

			subQuestion.setText(synonymsAndQuestion.getQuestionToUser().getQuestion());
			subQuestion.setCaretPosition(0);
			keywordPanel.setLayout(new MigLayout("wrap 1"));
			JCheckBox checkBox = new JCheckBox(synonymsAndQuestion.getSynonyms().get(0));
			checkBox.setSelected(true);
			checkBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (checkBox.isSelected()) {
						subQuestions.get(index).setEnabled(true);
						for (JTextField synonym : synonymsList.get(index)) {
							synonym.setEnabled(true);
						}
					}
					else {
						subQuestions.get(index).setEnabled(false);
						for (JTextField synonym : synonymsList.get(index)) {
							synonym.setEnabled(false);
						}
					}
				}
			});
			checkBoxes.add(checkBox);
			subQuestions.add(subQuestion);
			keywordPanel.add(subQuestion);
			keywordPanel.add(checkBox);
			for (int j=1; j<synonymsAndQuestion.getSynonyms().size(); j++) {
				JTextField synonyms = new JTextField(lengthOfTextField);
				synonyms.setText(synonymsAndQuestion.getSynonyms().get(j));
				keywordPanel.add(synonyms);
				synonymsList.get(i).add(synonyms);
			}
			
			for (int j=0; j<4-synonymsAndQuestion.getSynonyms().size(); j++) {
				
				JTextField synonyms = new JTextField(lengthOfTextField);
				keywordPanel.add(synonyms);
				synonymsList.get(i).add(synonyms);
			}
			

			keywordsPanel.add(keywordPanel);	
		}

		
		if (answer == null) {
		answer = new JTextArea(18,50);
		answer.setLineWrap(true);
		answer.setWrapStyleWord(true);
		answer.setBorder(null);

		answerPanel.setLayout(new MigLayout("wrap 1"));
		answerPanel.add(new JLabel("Verilecek cevap"), "align center");
		answerPanel.add(answer, "align center");
		answerPanel.add(insertButton, "align center");

		answer.setVisible(true);
		}

		frame.revalidate();
		frame.repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getSource()==question && e.getKeyCode() == KeyEvent.VK_ENTER) {
			generateQuestionsAndSynonyms();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
}