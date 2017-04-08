package GUI;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controllers.ImportDatabaseController;
import fileIO.XMLFileFilter;
import fileIO.XMLFileValidator;

public class ImportDatabasePanel extends JFileChooser {

	private ImportDatabaseController importDatabaseController;

	public ImportDatabasePanel() {
		JFileChooser jFileChooser=new JFileChooser();
		XMLFileFilter fileFilter = new XMLFileFilter();
		jFileChooser.setFileFilter(fileFilter);
		int result= jFileChooser.showOpenDialog(this);
		if(result==JFileChooser.APPROVE_OPTION)
		{
			File file=jFileChooser.getSelectedFile();
			XMLFileValidator fileValidator = new XMLFileValidator();
			String isValid = fileValidator.isValid(file);
			
			if (isValid.equals("valid")) {
				importDatabaseController = new ImportDatabaseController();
				importDatabaseController.importDatabase(file.getAbsolutePath());
				JOptionPane.showMessageDialog(new JPanel(), "Dosya başarıyla yüklendi.");
			}
			else {
				JOptionPane.showMessageDialog(new JPanel(), isValid);
			}
			
		}
	}


}
