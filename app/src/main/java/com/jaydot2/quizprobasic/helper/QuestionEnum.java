/**
 * 
 */
package com.jaydot2.quizprobasic.helper;

import java.util.Locale;

/**
 * @author james_r_bray
 *
 */
public enum QuestionEnum {
	A, B, C, D, E, Z;
	
	public static QuestionEnum get(String option) {
		if(option.toUpperCase(Locale.US) != null) {
			if(option.toUpperCase(Locale.US).equals("A")) {
				return QuestionEnum.A;
			}
			if(option.toUpperCase(Locale.US).equals("B")) {
				return QuestionEnum.B;
			}
			if(option.toUpperCase(Locale.US).equals("C")) {
				return QuestionEnum.C;
			}
			if(option.toUpperCase(Locale.US).equals("D")) {
				return QuestionEnum.D;
			}
			if(option.toUpperCase(Locale.US).equals("E")) {
				return QuestionEnum.E;
			}
		}
		return QuestionEnum.Z;
	}
}
