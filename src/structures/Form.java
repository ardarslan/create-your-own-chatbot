package structures;

import java.util.ArrayList;

import org.bson.types.ObjectId;

import enums.StringTypeEnum;

public class Form {
	
	private ObjectId _id = new ObjectId();
	private String formType;
	private ArrayList<String> questions;
	private ArrayList<String> answers;
	private ArrayList<StringTypeEnum> stringTypes;
	
	public Form() {
		
	}
	
	public ArrayList<String> getQuestions() {
		return questions;
	}
	public void setQuestions(ArrayList<String> questions) {
		this.questions = questions;
	}
	public ArrayList<String> getAnswers() {
		return answers;
	}
	public void setAnswers(ArrayList<String> answers) {
		this.answers = answers;
	}
	
	public Form(ArrayList<String> questions, ArrayList<String> answers, ArrayList<StringTypeEnum> stringTypes) {
		this.questions = questions;
		this.answers = answers;
		this.stringTypes = stringTypes;
	}
	
	public ObjectId get_id() {
		return _id;
	}
	public void set_id(ObjectId _id) {
		this._id = _id;
	}
	public String getFormType() {
		return formType;
	}
	public void setFormType(String formType) {
		this.formType = formType;
	}
	public ArrayList<StringTypeEnum> getStringTypes() {
		return stringTypes;
	}
	public void setStringTypes(ArrayList<StringTypeEnum> stringTypes) {
		this.stringTypes = stringTypes;
	}
}
