/**
 * 
 */
package com.jaydot2.quizprobasic.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author james_r_bray
 *
 */
public class Question implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1335873979606351992L;
	
	private ArrayList<String> questionOptions;
	private String question;
	private String selectedOption;
	private String solution;
	private int questionNumber;
	private String solutionText;

	/**
	 * 
	 */
	public Question() {}

	public ArrayList<String> getQuestionOptions() {
		return questionOptions;
	}

	public void setQuestionOptions(ArrayList<String> questionOptions) {
		this.questionOptions = questionOptions;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(int questionNumber) {
		this.questionNumber = questionNumber;
	}

	public String getSelectedOption() {
		return selectedOption;
	}

	public void setSelectedOption(String selectedOption) {
		this.selectedOption = selectedOption;
	}

	public String getSolutionText() {
		return solutionText;
	}

	public void setSolutionText(String solutionText) {
		this.solutionText = solutionText;
	}

}
