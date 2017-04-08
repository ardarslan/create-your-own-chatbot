package GUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import structures.InitializationThread;

public class OpeningPanel extends JFrame implements ActionListener {

	private JPanel panel;
	private JButton createEntryButton;
	private JButton createFormButton;
	private JButton importDatabaseButton;
	private JButton exportDatabaseButton;
	private JButton chatWithBotButton;
	private JButton searchEngineButton;
	private JButton exitButton;

	private CreateNewKeywordsAndAnswerPanel createEntryPanel;
	private CreateNewEmptyFormPanel createFormPanel;
	private ImportDatabasePanel importDatabasePanel;
	private ExportDatabasePanel exportDatabasePanel;
	private ChatPanel chatPanel;
	private SearchEnginePanel searchEnginePanel;

	public OpeningPanel() {
		panel = new JPanel();
		panel.setLayout(new MigLayout("wrap 1"));

		createEntryButton = new JButton("Create New Entry");
		createFormButton = new JButton("Create New Form");
		importDatabaseButton = new JButton("Import Multiple Entries");
		exportDatabaseButton = new JButton("Export All Entries");
		chatWithBotButton = new JButton("Chat with Bot");
		searchEngineButton = new JButton("Search Engine");
		exitButton = new JButton("Exit");
		
		createEntryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				createEntryPanel = new CreateNewKeywordsAndAnswerPanel();
			}
		});
		
		createFormButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createFormPanel = new CreateNewEmptyFormPanel();
			}	
		});

		importDatabaseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importDatabasePanel = new ImportDatabasePanel();
			}
		});
		
		exportDatabaseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				exportDatabasePanel = new ExportDatabasePanel();
			}
		});
		
		chatWithBotButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chatPanel = new ChatPanel();
			}
		});
		
		searchEngineButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchEnginePanel = new SearchEnginePanel();
			}
		});
		
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		panel.add(createEntryButton, "align center");
		panel.add(createFormButton, "align center");
		panel.add(importDatabaseButton, "align center");
		panel.add(exportDatabaseButton, "align center");
		panel.add(chatWithBotButton, "align center");
		panel.add(searchEngineButton, "align center");
		panel.add(exitButton, "align center");
		this.add(panel);
		
		this.setSize(215,265);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		new OpeningPanel();
		(new InitializationThread()).start();	
	}	
}

