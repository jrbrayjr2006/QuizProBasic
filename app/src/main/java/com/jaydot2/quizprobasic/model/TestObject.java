/**
 * 
 */
package com.jaydot2.quizprobasic.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author james_r_bray
 *
 */
public class TestObject implements Serializable {
	
	private static final long serialVersionUID = 3504698616482509143L;
	
	private String testID;
	private String testTitle;
	private List<Question> questions;
	private int index;
	private int score = -1;
	private String scoreText; // = "score: TRY ME! >";

	/**
	 * 
	 */
	public TestObject() {}

	public String getTestID() {
		return testID;
	}

	public void setTestID(String testID) {
		this.testID = testID;
	}

	public String getTestTitle() {
		return testTitle;
	}

	public void setTestTitle(String testTitle) {
		this.testTitle = testTitle;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getScoreText() {
		return scoreText;
	}

	public void setScoreText(String scoreText) {
		this.scoreText = scoreText;
	}

}
