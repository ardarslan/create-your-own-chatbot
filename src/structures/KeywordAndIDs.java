package structures;

import java.util.ArrayList;

import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

public class KeywordAndIDs
{
	@Id
    @Property("id")
    private String keyword;
    private ArrayList<String> IDList;
    
    public KeywordAndIDs() {
    	
    }
    
    

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeywordAndIDs other = (KeywordAndIDs) obj;
		if (keyword == null) {
			if (other.keyword != null)
				return false;
		} else if (!keyword.equals(other.keyword))
			return false;
		return true;
	}



	public KeywordAndIDs(String keyword, ArrayList<String> IDList)
    {
        this.setKeyword(keyword);
        this.IDList = IDList;
    }

    public ArrayList<String> getIDList() {
		return IDList;
	}

	public void setIDList(ArrayList<String> iDList) {
		IDList = iDList;
	}

	public void addNewKeyword(String keyword)
    {
        this.IDList.add(keyword);
    }

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}
