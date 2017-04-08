package fileIO;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public class XMLFileValidator implements FileValidator {

	public XMLFileValidator() {

	}

	public String isValid(File file) {
		try {
			SchemaFactory factory =
					SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new File("xmls/XMLSchemaDefinition.xsd"));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(file));
		} catch (IOException e){
			return "Exception: "+e.getMessage();
		}catch(SAXException e1){
			return "SAX Exception: "+e1.getMessage();
		}

		return "valid";
	}
}
