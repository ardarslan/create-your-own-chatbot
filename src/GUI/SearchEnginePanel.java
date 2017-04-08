package GUI;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controllers.SearchEngineController;
import net.miginfocom.swing.MigLayout;

public class SearchEnginePanel implements KeyListener {
	private SearchEngineController searchEngineController;
	private JFrame frame;
	private JPanel searchPanel;
	private JPanel resultPanel;
	private JTextField searchBar;
	private JButton searchButton;
	private ArrayList<JLabel> labels;

	public SearchEnginePanel() {
		searchEngineController = new SearchEngineController();
		frame = new JFrame("Search Engine");
		frame.setLayout(new MigLayout("wrap 1"));
		searchPanel = new JPanel();
		
		searchBar = new JTextField(30);
		searchButton = new JButton("Search");
		labels = new ArrayList<JLabel>();
		
		searchBar.addKeyListener(this);

		searchButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				showResults();
			}

		});
		
		searchBar.requestFocus();
		searchPanel.add(searchBar, "align center");
		searchPanel.add(searchButton, "align center");

		frame.add(searchPanel, "align center");
		
		frame.setSize(450,550);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getSource()==searchBar && e.getKeyCode() == KeyEvent.VK_ENTER) {
			showResults();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void showResults() {
		if (resultPanel != null) frame.remove(resultPanel);
		resultPanel = new JPanel();
		for (JLabel label : labels) {
			label.setVisible(false);
			resultPanel.remove(label);
		}
		resultPanel.setLayout(new MigLayout("wrap 1"));
		JLabel searchResults = new JLabel("Search Results:");
		searchResults.setFont(new Font("Arial", Font.BOLD, 25));
		resultPanel.add(searchResults);
		frame.add(resultPanel, "align center");
		searchEngineController = new SearchEngineController();
		searchEngineController.searchAtDatabase(searchBar.getText()); 
		ArrayList<String> questions = searchEngineController.getQuestions();
		ArrayList<String> answers = searchEngineController.getAnswers();
		for (int i=0; i<questions.size(); i++) {
			final int index = i;
			JLabel label = new JLabel(i+1 + ") " + questions.get(i));
			labels.add(label);
			resultPanel.add(label);
			label.setFont(new Font("Arial", Font.PLAIN, 20));
			Font font = label.getFont();
			Map attributes = font.getAttributes();
			attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			label.setFont(font.deriveFont(attributes));
			label.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					JOptionPane.showMessageDialog(new JPanel(), answers.get(index));
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseEntered(MouseEvent e) {
					label.setForeground(Color.BLUE);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					label.setForeground(Color.BLACK);
				}
			});
		}
		resultPanel.revalidate();
		resultPanel.repaint();
		frame.revalidate();
		frame.repaint();
	}
}


