package GUI;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controllers.ExportDatabaseController;
import fileIO.XMLFileChooser;

public class ExportDatabasePanel extends JFileChooser {

	private ExportDatabaseController exportDatabaseController;

	public ExportDatabasePanel() {
		
		XMLFileChooser fileSaver = new XMLFileChooser();
		int result = fileSaver.showSaveDialog(new JFrame());
		if(result == XMLFileChooser.APPROVE_OPTION){
			exportDatabaseController = new ExportDatabaseController();
			exportDatabaseController.exportDatabase(fileSaver.getSelectedFile().getAbsolutePath());
			JOptionPane.showMessageDialog(new JPanel(), "Dosya başarıyla kaydedildi.");
		}
	}
}