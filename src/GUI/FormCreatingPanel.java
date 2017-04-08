package GUI;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

import controllers.CreateNewEmptyFormController;
import enums.StringTypeEnum;
import net.miginfocom.swing.MigLayout;
import structures.Form;

public class FormCreatingPanel
{
	private JFrame frame;
	private JPanel mainPanel;
	private JButton addNewQuestionButton;
	private JButton insertButton;
	private JTextField firstQuestion;
	private JComboBox<String> typeOfFirstQuestion;
	private JPanel questionsAndTypesPanel;
	private JPanel buttonPanel;
	private CreateNewEmptyFormController createNewEmptyFormController;
	private ArrayList<JTextField> questions;
	private ArrayList<JComboBox<String>> types;

	public FormCreatingPanel(CreateNewEmptyFormController createNewEmptyFormController, JFrame previousFrame) {
		this.createNewEmptyFormController = createNewEmptyFormController;
		questions = new ArrayList<JTextField>();
		types = new ArrayList<JComboBox<String>>();
		frame = new JFrame("Create New Form");
		mainPanel = new JPanel();
		buttonPanel = new JPanel();
		addNewQuestionButton = new JButton("Add New Question to Form");
		insertButton = new JButton("Insert to Database");
		firstQuestion = new JTextField(30);
		typeOfFirstQuestion = new JComboBox<String>();

		questionsAndTypesPanel = new JPanel();
		questionsAndTypesPanel.setLayout(new MigLayout("wrap 1"));

		mainPanel.setLayout(new MigLayout("wrap 1"));
		
		questions.add(firstQuestion);
		types.add(typeOfFirstQuestion);

		typeOfFirstQuestion.addItem("Name and Surname");
		typeOfFirstQuestion.addItem("Telephone Number");
		typeOfFirstQuestion.addItem("E-Mail Address");
		typeOfFirstQuestion.addItem("Turkish Identification Number");
		typeOfFirstQuestion.addItem("Address");
		typeOfFirstQuestion.addItem("Date of Birth");
		typeOfFirstQuestion.addItem("Other");	
		
		typeOfFirstQuestion.setSelectedItem("Other");

		typeOfFirstQuestion.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getItem().equals("Name and Surname")) {
					firstQuestion.setText("Adınızı ve soyadınızı aralarında bir boşluk bırakarak yazabilir misiniz?");
					firstQuestion.setCaretPosition(0);
				}
				else if (e.getItem().equals("Telephone Number")) {
					firstQuestion.setText("Cep telefon numaranızı başında sıfır olacak şekilde yazabilir misiniz?");
					firstQuestion.setCaretPosition(0);
				}
				else if (e.getItem().equals("E-Mail Address")) {
					firstQuestion.setText("Size ulaşabileceğimiz bir e-mail adresinizi yazabilir misiniz?");
					firstQuestion.setCaretPosition(0);
				}
				else if (e.getItem().equals("Turkish Identification Number")) {
					firstQuestion.setText("TC Kimlik numaranızı yazabilir misiniz?");
					firstQuestion.setCaretPosition(0);
				}
				else if (e.getItem().equals("Address")) {
					firstQuestion.setText("Açık adresinizi yazabilir misiniz?");
					firstQuestion.setCaretPosition(0);
				}
				else if (e.getItem().equals("Date of Birth")) {
					firstQuestion.setText("Doğum tarihinizi GG/AA/YYYY şeklinde yazabilir misiniz?");
					firstQuestion.setCaretPosition(0);
				}
			}

		});

		JPanel firstQuestionPanel = new JPanel();
		firstQuestionPanel.add(firstQuestion);
		firstQuestionPanel.add(typeOfFirstQuestion);

		questionsAndTypesPanel.add(firstQuestionPanel, "align center");

		buttonPanel.add(addNewQuestionButton);
		buttonPanel.add(insertButton);

		mainPanel.add(questionsAndTypesPanel);
		mainPanel.add(buttonPanel, "align center");

		addNewQuestionButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JTextField newQuestion = new JTextField(30);
				JPanel newPanel = new JPanel();
				newPanel.add(newQuestion);
				questions.add(newQuestion);
				JComboBox<String> newType = new JComboBox<String>();

				newType.addItem("Name and Surname");
				newType.addItem("Telephone Number");
				newType.addItem("E-Mail Address");
				newType.addItem("Turkish Identification Number");
				newType.addItem("Address");
				newType.addItem("Date of Birth");
				newType.addItem("Other");
				
				newType.setSelectedItem("Other");
				
				newType.addItemListener(new ItemListener() {

					@Override
					public void itemStateChanged(ItemEvent e) {
						if (e.getItem().equals("Name and Surname")) {
							newQuestion.setText("Adınızı ve soyadınızı aralarında bir boşluk bırakarak yazabilir misiniz?");
							newQuestion.setCaretPosition(0);
						}
						else if (e.getItem().equals("Telephone Number")) {
							newQuestion.setText("Cep telefon numaranızı başında sıfır olacak şekilde yazabilir misiniz?");
							newQuestion.setCaretPosition(0);
						}
						else if (e.getItem().equals("E-Mail Address")) {
							newQuestion.setText("Size ulaşabileceğimiz bir e-mail adresinizi yazabilir misiniz?");
							newQuestion.setCaretPosition(0);
						}
						else if (e.getItem().equals("Turkish Identification Number")) {
							newQuestion.setText("TC Kimlik numaranızı yazabilir misiniz?");
							newQuestion.setCaretPosition(0);
						}
						else if (e.getItem().equals("Address")) {
							newQuestion.setText("Açık adresinizi yazabilir misiniz?");
							newQuestion.setCaretPosition(0);
						}
						else if (e.getItem().equals("Date of Birth")) {
							newQuestion.setText("Doğum tarihinizi GG/AA/YYYY şeklinde yazabilir misiniz?");
							newQuestion.setCaretPosition(0);
						}
					}
				});

				
				types.add(newType);
				newPanel.add(newType);

				questionsAndTypesPanel.add(newPanel);

				frame.revalidate();
				frame.repaint();
				frame.pack();
				frame.setLocationRelativeTo(null);
			}
		});

		insertButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> questionString = new ArrayList<String>();
				ArrayList<StringTypeEnum> typesString = new ArrayList<StringTypeEnum>();
				for (JTextField textField : questions) {
					String temp = textField.getText();
					if (!temp.isEmpty())
					questionString.add(temp);
				}
				for (JComboBox<String> comboBox : types) {
					String selected = comboBox.getSelectedItem().toString();
					if (selected.equals("Name and Surname")) {
						typesString.add(StringTypeEnum.NAMEANDSURNAME);
					}
					else if (selected.equals("Telephone Number")) {
						typesString.add(StringTypeEnum.TELEPHONE);
					}
					else if (selected.equals("E-Mail Address")) {
						typesString.add(StringTypeEnum.EMAIL);
					}
					else if (selected.equals("Turkish Identification Number")) {
						typesString.add(StringTypeEnum.TC);
					}
					else if (selected.equals("Address")) {
						typesString.add(StringTypeEnum.ADDRESS);
					}
					else if (selected.equals("Date of Birth")) {
						typesString.add(StringTypeEnum.BIRTH);
					}
					else if (selected.equals("Other")) {
						typesString.add(StringTypeEnum.OTHER);
					}	
				}
				createNewEmptyFormController.createNewEmptyForm(questionString, typesString);

				createNewEmptyFormController.insertAllToDatabase();
				JOptionPane.showMessageDialog(new JPanel(), "Form başarıyla kaydedildi.");
				previousFrame.dispose();
				frame.dispose();
			}
		});
		frame.add(mainPanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);	
	}
}
