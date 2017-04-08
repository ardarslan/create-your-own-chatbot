package GUI;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

import controllers.ChatWithBotController;
import net.miginfocom.swing.MigLayout;

public class ChatPanel extends JFrame implements ActionListener, KeyListener {

	private ChatWithBotController chatWithBotController;
	JPanel panel;
	JTextArea messageArea;
	JScrollPane scrollPane;
	JTextField sendArea;
	JButton sendButton;
	JPanel sendPanel;

	public ChatPanel() {
		super("Chat with Bot");
		
		chatWithBotController = new ChatWithBotController();
		sendArea = new JTextField(35);
		sendButton = new JButton("Send");
		panel = new JPanel();
		sendPanel = new JPanel();
		messageArea = new JTextArea(20,40);
		messageArea.setEditable (false);
		scrollPane = new JScrollPane(messageArea);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		panel.setLayout(new MigLayout("wrap 1"));
		panel.add(scrollPane, "align center");
		sendPanel.add(sendArea);
		sendPanel.add(sendButton);
		panel.add(sendPanel);

		
		messageArea.setFont(new Font("Arial", Font.PLAIN, 20));
		messageArea.setLineWrap(true);
		messageArea.setWrapStyleWord(true);
		messageArea.setText("Bot: Merhaba, size nasıl yardımcı olabilirim?");
		
		sendArea.addKeyListener(this);
		sendArea.setFont(new Font("Arial", Font.PLAIN, 20));
		
		
		
		sendButton.addActionListener(this);
		JScrollBar vertical = scrollPane.getVerticalScrollBar();
		vertical.setValue( vertical.getMaximum() );
		
		
		this.setSize(800,600);
		this.add(panel);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		sendArea.requestFocus();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getSource()==sendArea && e.getKeyCode() == KeyEvent.VK_ENTER) {
			chatWithBotController.answerToUserQuestion(sendArea.getText());
			messageArea.append("\nSiz: " + sendArea.getText());
			sendArea.setText("");
			messageArea.append("\nBot: " + chatWithBotController.getAnswerToReturn());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object theButton = e.getSource();
		if (theButton == sendButton) {
			chatWithBotController.answerToUserQuestion(sendArea.getText());
			messageArea.append("\nSiz: " + sendArea.getText());
			sendArea.setText("");
			messageArea.append("\nBot: " + chatWithBotController.getAnswerToReturn());
		}
	}
}
