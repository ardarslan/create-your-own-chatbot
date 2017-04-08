package fileIO;

import java.io.File;

public class XMLFileFilter extends javax.swing.filechooser.FileFilter {
	
	/**
	 * @param file: File object.
	 * @return Returns true if the <code>file</code> is accepted; otherwise returns false.
	 */
	public boolean accept(File file) {
		String filename = file.getName();
		return filename.endsWith(".xml");
	}
	
	/**
	 * @return Returns "*.xml" for File accepted File types.
	 */
	public String getDescription() {
		return "*.xml";
	}
}
