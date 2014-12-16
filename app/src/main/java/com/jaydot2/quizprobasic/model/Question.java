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
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String optionE;


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

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getOptionE() {
        return optionE;
    }

    public void setOptionE(String optionE) {
        this.optionE = optionE;
    }
}
