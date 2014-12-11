/**
 * 
 */
package com.jaydot2.quizprobasic;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaydot2.quizprobasic.helper.QuestionEnum;
import com.jaydot2.quizprobasic.model.Question;
import com.jaydot2.quizprobasic.model.TestObject;

/**
 * @author james_r_bray
 *
 */
public class TestQuestionFragment extends Fragment {
	
	private static final String DEBUG_TAG = "Gestures";
	private static final String TAG = "TestQuestionFragment";
	
	private static final int MAX_NUM_QUESTIONS = 10;
	private static final int MIN_NUM_QUESTIONS = 1;
	
	/**
	 * Flag to determine if the test has been completed.
	 */
	private boolean testCompleted = false;
	
	private GestureDetectorCompat mDetector; 
	//private TestObject myTest;
	private TestObject selectedTest;
	//List<Question> questions;
	
	private TextView timerValue;
	private TextView questionText;
	private Button endTestButton;
	private Button aButton;
	private Button bButton;
	private Button cButton;
	private Button dButton;
	private Button eButton;
	private Button solutionButton;
	private ImageView questionImageView;
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime10 = 600000;
	//long updatedTime10 = 10000;  // for testing only, remove when ready to ship
	private int testIndex;
	private int currentIndex;
	private CountDownTimer timer;
	
	private int mNumberOfCorrectAnswers;
	private String mReviewQuestions;
	private String mTestTitle;
	
	public interface OnTestListener {
		public void onEndTestClick(int _numCorrect, String _review, String _title);
		
		public void onShowSoluton(String _solutionText);
	}
	
	OnTestListener mCallback;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		super.onCreateView(inflater, root, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_question, root, false);
		
		timerValue = (TextView)v.findViewById(R.id.timerTextView);
		questionText = (TextView)v.findViewById(R.id.questionTextView);
		endTestButton = (Button)v.findViewById(R.id.endTestBtn);
		aButton = (Button)v.findViewById(R.id.buttonA);
		bButton = (Button)v.findViewById(R.id.buttonB);
		cButton = (Button)v.findViewById(R.id.buttonC);
		dButton = (Button)v.findViewById(R.id.buttonD);
		eButton = (Button)v.findViewById(R.id.buttonE);
		questionImageView = (ImageView)v.findViewById(R.id.questionImageView);
		solutionButton = (Button)v.findViewById(R.id.buttonSolution);
		
		selectedTest = (TestObject)getArguments().getSerializable(MainActivity.TEST);
		mTestTitle = selectedTest.getTestTitle();
		
		testIndex = getArguments().getInt("index");
		currentIndex = 0;
		
		// initialize values for results
		mNumberOfCorrectAnswers = 0;
		
		questionText.setText("Question " + selectedTest.getQuestions().get(0).getQuestionNumber() + "/10");
		// was using myTest here 11-28-2014
		selectedTest.setQuestions(selectedTest.getQuestions());
		
		endTestButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				calcTestResults();
				endTestButton.setText(getActivity().getResources().getString(R.string.see_results));
		    	aButton.setEnabled(false);
		    	bButton.setEnabled(false);
		    	cButton.setEnabled(false);
		    	dButton.setEnabled(false);
		    	eButton.setEnabled(false);
		    	testCompleted = true;
		    	solutionButton.setEnabled(true);
				timer.cancel();
				mCallback.onEndTestClick(mNumberOfCorrectAnswers, mReviewQuestions, mTestTitle);
				
			}
			
		});
		
		aButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				defaultButtonTextColor();
				updateCurrentQuestion("A");
				updateButtonColor(aButton);
			}
			
		});
		bButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				defaultButtonTextColor();
				updateCurrentQuestion("B");
				updateButtonColor(bButton);
			}
			
		});
		cButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				defaultButtonTextColor();
				updateCurrentQuestion("C");
				updateButtonColor(cButton);
			}
			
		});
		dButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				defaultButtonTextColor();
				updateCurrentQuestion("D");
				updateButtonColor(dButton);
			}
			
		});
		eButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				defaultButtonTextColor();
				updateCurrentQuestion("E");
				updateButtonColor(eButton);
			}
			
		});
		solutionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String _solutionText = selectedTest.getQuestions().get(currentIndex).getSolutionText();
				mCallback.onShowSoluton(_solutionText);
			}});
		
		mDetector = new GestureDetectorCompat(getActivity(), new MyGestureListener());
		
		v.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d(DEBUG_TAG, "Set onTouch event...");
				mDetector.onTouchEvent(event);
				v.performClick();
				return true;
			}
			
		});
		
		timer = new CountDownTimer(updatedTime10, 1000) {

		     public void onTick(long millisUntilFinished) {
		    	 int secs = (int) (millisUntilFinished / 1000);
		    	 int mins = secs / 60;
		    	 int displaySecs = secs - (mins * 60);
		    	 timerValue.setText("remaining time: " + "" + mins + ":" + String.format("%02d", displaySecs));
		     }

		     public void onFinish() {
		    	 timerValue.setText("The time expired!");
		    	 timerValue.setTextColor(getActivity().getResources().getColor(R.color.red));
		    	 endTestButton.setText(getActivity().getResources().getString(R.string.see_results));
		    	 aButton.setEnabled(false);
		    	 bButton.setEnabled(false);
		    	 cButton.setEnabled(false);
		    	 dButton.setEnabled(false);
		    	 eButton.setEnabled(false);
		    	 testCompleted = true;
		    	 solutionButton.setEnabled(true);
		    	 endTestAction();
		     }
		  }.start();
		  updateImage();

		return v;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallback = (OnTestListener) activity;
	}
	
	private void defaultButtonTextColor() {
		aButton.setTextColor(getActivity().getResources().getColor(R.color.black));
		bButton.setTextColor(getActivity().getResources().getColor(R.color.black));
		cButton.setTextColor(getActivity().getResources().getColor(R.color.black));
		dButton.setTextColor(getActivity().getResources().getColor(R.color.black));
		eButton.setTextColor(getActivity().getResources().getColor(R.color.black));
	}
	
	private void updateButtonColor(Button targetButton) {
		targetButton.setTextColor(getActivity().getResources().getColor(R.color.green));
	}
	
	private void highlightIncorrectAnswersByColor(Button targetButton) {
		targetButton.setTextColor(getActivity().getResources().getColor(R.color.red));
	}
	
	private void highlightCorrectAnswerByColorAtEndOfTest(Button targetButton) {
		targetButton.setTextColor(getActivity().getResources().getColor(R.color.violet));
	}
	
	private void updateCurrentQuestion(String _selectedOption) {
		selectedTest.getQuestions().get(currentIndex).setSelectedOption(_selectedOption);
	}
	
	private void updateImage() {
		try 
		{
			int t = testIndex + 1;
			int q = currentIndex + 1;
			String imageName = "pictures/" + t + "-" + q + ".jpg";
			Log.d(DEBUG_TAG, imageName);
		    // get input stream
		    InputStream ims = getActivity().getAssets().open(imageName);
		    // load image as Drawable
		    Drawable d = Drawable.createFromStream(ims, null);
		    // set image to ImageView
		    questionImageView.setImageDrawable(d);
		    questionImageView.setMinimumWidth(800);
		    questionImageView.setMinimumHeight(500);
		}
		catch(IOException ex) 
		{
			Log.e(TAG, ex.getMessage());
		    return;
		}
	}
	
	/**
	 * Clear all data from tests and reset them for the next test
	 */
	protected void clearAndResetTests() {
		defaultButtonTextColor();
		//reset all question selectedOption properties to null
		for(Question q : selectedTest.getQuestions()) {
			q.setSelectedOption(null);
		}
		selectedTest = null;
		timer.cancel();
	}
	
	
	private void endTestAction() {
        String opts = selectedTest.getQuestions().get(currentIndex).getSelectedOption();
        if((opts != null) && !opts.equals(MainActivity.DEFAULT_Z)) {
        	if(opts.equals("A")) {
        		updateButtonColor(aButton);
        	}
        	if(opts.equals("B")) {
        		updateButtonColor(bButton);
        	}
        	if(opts.equals("C")) {
        		updateButtonColor(cButton);
        	}
        	if(opts.equals("D")) {
        		updateButtonColor(dButton);
        	}
        	if(opts.equals("E")) {
        		updateButtonColor(eButton);
        	}
        	if(testCompleted) {
        		if(!opts.equals(selectedTest.getQuestions().get(currentIndex).getSolution())) {
        			String message = opts + " is incorrect.  The correct answer is " + selectedTest.getQuestions().get(currentIndex).getSolution();
        			switch(QuestionEnum.get(opts.toUpperCase(Locale.US))) {
        			case A:
        				highlightIncorrectAnswersByColor(aButton);
        				break;
        			case B:
        				highlightIncorrectAnswersByColor(bButton);
        				break;
        			case C:
        				highlightIncorrectAnswersByColor(cButton);
        				break;
        			case D:
        				highlightIncorrectAnswersByColor(dButton);
        				break;
        			case E:
        				highlightIncorrectAnswersByColor(eButton);
        				break;
        			case Z:
        				break;
        			}
        			// highlight the correct answer
        			switch(QuestionEnum.get(selectedTest.getQuestions().get(currentIndex).getSolution().toUpperCase(Locale.US))) {
        			case A:
        				highlightCorrectAnswerByColorAtEndOfTest(aButton);
        				break;
        			case B:
        				highlightCorrectAnswerByColorAtEndOfTest(bButton);
        				break;
        			case C:
        				highlightCorrectAnswerByColorAtEndOfTest(cButton);
        				break;
        			case D:
        				highlightCorrectAnswerByColorAtEndOfTest(dButton);
        				break;
        			case E:
        				highlightCorrectAnswerByColorAtEndOfTest(eButton);
        				break;
        			case Z:
        				break;
        			}
        			Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        		}
        	}
        } else {
        	if(testCompleted) {
        		// highlight the correct answer
    			switch(QuestionEnum.get(selectedTest.getQuestions().get(currentIndex).getSolution().toUpperCase(Locale.US))) {
    			case A:
    				highlightCorrectAnswerByColorAtEndOfTest(aButton);
    				break;
    			case B:
    				highlightCorrectAnswerByColorAtEndOfTest(bButton);
    				break;
    			case C:
    				highlightCorrectAnswerByColorAtEndOfTest(cButton);
    				break;
    			case D:
    				highlightCorrectAnswerByColorAtEndOfTest(dButton);
    				break;
    			case E:
    				highlightCorrectAnswerByColorAtEndOfTest(eButton);
    				break;
    			case Z:
    				break;
    			}
        		String message = " You did not answer this question.  The correct answer is " + selectedTest.getQuestions().get(currentIndex).getSolution();
    			Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        	}
        }
	}

	/**
	 * 
	 * @author james_r_bray
	 *
	 */
	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures"; 
        int x = 1;
        
        @Override
        public boolean onDown(MotionEvent event) { 
            Log.d(DEBUG_TAG,"onDown: " + event.toString()); 
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, 
                float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());
            //Toast.makeText(getActivity(), DEBUG_TAG, Toast.LENGTH_SHORT).show();
            float x1 = event1.getX();
            float x2 = event2.getX();
            // Left to Right swipe action
            if (x2 > x1) {
                //Toast.makeText(getActivity(), "Left to Right swipe [Next]", Toast.LENGTH_SHORT).show();
            	if(x > MIN_NUM_QUESTIONS) {
                	x--;
                	// reset button colors when going to new question
                	defaultButtonTextColor();
                }
            }
            // Right to left swipe action               
            else {
                //Toast.makeText(getActivity(), "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show();
                if(x < MAX_NUM_QUESTIONS) {
                	x++;
                	// reset button colors when going to new question
                	defaultButtonTextColor();
                }
            }
            currentIndex = x - 1;
            updateImage();
            String opts = selectedTest.getQuestions().get(currentIndex).getSelectedOption();
            if((opts != null) && !opts.equals(MainActivity.DEFAULT_Z)) {
            	if(opts.equals("A")) {
            		updateButtonColor(aButton);
            	}
            	if(opts.equals("B")) {
            		updateButtonColor(bButton);
            	}
            	if(opts.equals("C")) {
            		updateButtonColor(cButton);
            	}
            	if(opts.equals("D")) {
            		updateButtonColor(dButton);
            	}
            	if(opts.equals("E")) {
            		updateButtonColor(eButton);
            	}
            	if(testCompleted) {
            		if(!opts.equals(selectedTest.getQuestions().get(currentIndex).getSolution()) && !opts.equals(MainActivity.DEFAULT_Z)) {
            			String message = opts + " is incorrect.  The correct answer is " + selectedTest.getQuestions().get(currentIndex).getSolution();
            			switch(QuestionEnum.get(opts.toUpperCase(Locale.US))) {
            			case A:
            				highlightIncorrectAnswersByColor(aButton);
            				break;
            			case B:
            				highlightIncorrectAnswersByColor(bButton);
            				break;
            			case C:
            				highlightIncorrectAnswersByColor(cButton);
            				break;
            			case D:
            				highlightIncorrectAnswersByColor(dButton);
            				break;
            			case E:
            				highlightIncorrectAnswersByColor(eButton);
            				break;
            			case Z:
            				break;
            			}
            			// highlight the correct answer
            			switch(QuestionEnum.get(selectedTest.getQuestions().get(currentIndex).getSolution().toUpperCase(Locale.US))) {
            			case A:
            				highlightCorrectAnswerByColorAtEndOfTest(aButton);
            				break;
            			case B:
            				highlightCorrectAnswerByColorAtEndOfTest(bButton);
            				break;
            			case C:
            				highlightCorrectAnswerByColorAtEndOfTest(cButton);
            				break;
            			case D:
            				highlightCorrectAnswerByColorAtEndOfTest(dButton);
            				break;
            			case E:
            				highlightCorrectAnswerByColorAtEndOfTest(eButton);
            				break;
            			case Z:
            				break;
            			}
            			Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            		}
            	}
            } else {
            	if(testCompleted) {
            		// highlight the correct answer
        			switch(QuestionEnum.get(selectedTest.getQuestions().get(currentIndex).getSolution().toUpperCase(Locale.US))) {
        			case A:
        				highlightCorrectAnswerByColorAtEndOfTest(aButton);
        				break;
        			case B:
        				highlightCorrectAnswerByColorAtEndOfTest(bButton);
        				break;
        			case C:
        				highlightCorrectAnswerByColorAtEndOfTest(cButton);
        				break;
        			case D:
        				highlightCorrectAnswerByColorAtEndOfTest(dButton);
        				break;
        			case E:
        				highlightCorrectAnswerByColorAtEndOfTest(eButton);
        				break;
        			case Z:
        				break;
        			}
            		String message = " You did not answer this question.  The correct answer is " + selectedTest.getQuestions().get(currentIndex).getSolution();
        			Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            	}
            }
            String questionTitle = "Question " + x + "/10";
            questionText.setText(questionTitle);
            return true;
        }
    }
	
	private void calcTestResults() {
		StringBuffer sb = new StringBuffer();
		List<Question> lQuestions = selectedTest.getQuestions();
		mNumberOfCorrectAnswers = 0;
		for(Question q : lQuestions) {
			// getSolution() will never return a null value, whereas getSelectedOption() can return a null value
			if(q.getSolution().equals(q.getSelectedOption())) {
				mNumberOfCorrectAnswers++;
			} else {
				//mReviewQuestionsList.add(String.valueOf(q.getQuestionNumber()));
				if(sb.length() < 1) {
					sb.append(String.valueOf(q.getQuestionNumber()));
				} else {
					sb.append(", ");
					sb.append(String.valueOf(q.getQuestionNumber()));
				}
				mReviewQuestions = sb.toString();
			}
		}
	}
}
