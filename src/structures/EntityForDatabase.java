package structures;

import java.util.ArrayList;

public class EntityForDatabase {

	private String type;
	private ArrayList<String> values;
	
	public EntityForDatabase(String type, ArrayList<String> values) {
		super();
		this.type = type;
		this.values = values;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<String> getValues() {
		return values;
	}

	public void setValues(ArrayList<String> values) {
		this.values = values;
	}
}
