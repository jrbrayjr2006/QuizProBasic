/**
 * 
 */
package com.jaydot2.quizprobasic;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author james_r_bray
 *
 */
public class ResultsFragment extends Fragment {
	
	private TextView mPerformanceTextView;
	private TextView mScoreTextView;
	private TextView mReviewOutputTextView;
	private Button mStartNewTestBtn;
	private int mNumberCorrect;
	private String mIncorrectAnswers;
	
	OnResultsListener mCallback;
	
	public interface OnResultsListener {
		public void onShowTestList();
	}

	/**
	 * 
	 */
	public ResultsFragment() {}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		super.onCreateView(inflater, root, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_result, root, false);
		
		mNumberCorrect = getArguments().getInt(MainActivity.NUM_CORRECT_KEY);
		mIncorrectAnswers = getArguments().getString(MainActivity.REVIEW_LIST_KEY);
		
		String evalMessege = getTestEvaluation(mNumberCorrect);
		
		mScoreTextView = (TextView)v.findViewById(R.id.scoreTextView);
		mScoreTextView.setText(getActivity().getResources().getString(R.string.correct_answers_text) + " " + mNumberCorrect + "/10");
		
		mReviewOutputTextView = (TextView)v.findViewById(R.id.reviewOutputTextView);
		mReviewOutputTextView.setText(mIncorrectAnswers);
		
		mPerformanceTextView = (TextView)v.findViewById(R.id.performanceTextView);
		mPerformanceTextView.setText(evalMessege);
		
		mStartNewTestBtn = (Button)v.findViewById(R.id.startNewTestBtn);
		mStartNewTestBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCallback.onShowTestList();
			}
		});
		return v;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallback = (OnResultsListener)activity;
	}
	
	private String getTestEvaluation(int _results) {
		String messege = getActivity().getResources().getString(R.string.average_performance);
		if(_results < 5) {
			messege = getActivity().getResources().getString(R.string.low_performance);
		}
		if(_results >= 8) {
			messege = getActivity().getResources().getString(R.string.excellent_performance);
		}
		return messege;
	}

}
