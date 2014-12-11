/**
 * 
 */
package com.jaydot2.quizprobasic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.jaydot2.quizprobasic.adapter.TestListAdapter;
import com.jaydot2.quizprobasic.model.Question;
import com.jaydot2.quizprobasic.model.TestObject;

/**
 * @author james_r_bray
 *
 */
public class TestListFragment extends Fragment {
	TextView test1TextView;
	private ListView mTestListView;
	
	private TestListAdapter mTestListAdapter;
	private ArrayList<String> mTestList;
	
	private ArrayList<TestObject> testObjects;
	
	public interface OnTestSelectedListener {
		public void onTestSelected(int index);
	}
	
	OnTestSelectedListener mListener; 
	
	private final String ERROR = "TestListFragment Error";
	private final String TAG = "TestListFragment";
	private final static int MAX_NUMBER_OF_QUESTIONS = 10;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * 
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_question_list, root, false);
		
		//test1TextView = (TextView)v.findViewById();
		
		mTestListView = (ListView)v.findViewById(R.id.questionsListView);
		//load the data from the config.xml into TestObjects
		testObjects = loadAndParseLocalXml();
		readScoreXmlFile();
		
		mTestList = new ArrayList<String>();
		for(TestObject to : testObjects) {
			mTestList.add(to.getTestTitle());
		}
		
		mTestListAdapter = new TestListAdapter(getActivity(), testObjects);
		mTestListView.setAdapter(mTestListAdapter);
		
		mTestListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mListener.onTestSelected(position);
				Log.d(TAG, mTestList.get(position));
				//Toast.makeText(getActivity(), mTestList.get(position), Toast.LENGTH_SHORT).show();
			}
			
		});
		
		return v;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnTestSelectedListener)activity;
		} catch(ClassCastException cce) {
			Log.e(ERROR, "Error occurred while casting activity to OnTestSelectedListener");
		}
	}
	
	public ArrayList<TestObject> loadAndParseLocalXml() {
		Log.d(TAG, "loadAndParseLocalXml");
		//boolean flag = false;
		ArrayList<TestObject> tests = new ArrayList<TestObject>();
		XmlResourceParser configXml = getActivity().getResources().getXml(R.xml.config);
		try {
			Log.d(TAG, "Start parsing XML...");
			int eventType = configXml.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT) {
				//Log.d(TAG, "In while loop...");
				if(eventType == XmlPullParser.START_DOCUMENT) {
					
				}
				if(eventType == XmlPullParser.START_TAG) {
					String element = configXml.getName();
					Log.d(TAG, "XML element is " + element);
					if(element.equals("test")) {
						//configXml.next();
						TestObject to = new TestObject();
						List<Question> questions = new ArrayList<Question>();
						Log.d(TAG, "ID of the test is " + configXml.getAttributeValue(0));
						Log.d(TAG, "Title of the test is " + configXml.getAttributeValue(1));
						to.setTestTitle(configXml.getAttributeValue(1));
						// iterate through questions
						for(int i = 0; i < MAX_NUMBER_OF_QUESTIONS; i++) {
							configXml.next();
							if((configXml.getName() != null) && (configXml.getName().equals("question"))) {
								String _solution =  configXml.getAttributeValue(1);
								String strIndex = configXml.getAttributeValue(0);
								int _index = Integer.parseInt(strIndex);
								Question myQ = new Question();
								configXml.next();
								if(configXml.getName().equals("question_text")) {
									configXml.next();
									String _questionText = configXml.getText().toString();
									Log.d(TAG, "The question is: " + _questionText);
									configXml.next();
									configXml.next();
									configXml.next();
									String _imagePath = configXml.getText().toString();
									Log.d(TAG, "The image path is: " + _imagePath);
									configXml.next();
									configXml.next();
									configXml.next();
									String optA = configXml.getText().toString();
									configXml.next();
									configXml.next();
									configXml.next();
									String optB = configXml.getText().toString();
									configXml.next();
									configXml.next();
									configXml.next();
									String optC = configXml.getText().toString();
									configXml.next();
									configXml.next();
									configXml.next();
									String optD = configXml.getText().toString();
									configXml.next();
									configXml.next();
									configXml.next();
									String optE = configXml.getText().toString();
									Log.d(TAG, "The options are: " + optA + ", " + optB + ", " + optC + ", " + optD + ", " + optE);
									ArrayList<String> questionOptions = new ArrayList<String>();
									questionOptions.add(optA);
									questionOptions.add(optB);
									questionOptions.add(optC);
									questionOptions.add(optD);
									questionOptions.add(optE);
									
									myQ.setQuestionOptions(questionOptions);
									myQ.setQuestion(_questionText);
									myQ.setSolution(_solution); 
									myQ.setQuestionNumber(_index);
									Log.d(TAG, "The solution is: " + _solution);
									configXml.next();
									configXml.next();
								}
								questions.add(myQ);
							}
						}
						to.setQuestions(questions);
						tests.add(to);
					} 
				}
				if(eventType == XmlPullParser.END_TAG) {
					
				}
				if(eventType != XmlPullParser.END_DOCUMENT) {
					configXml.next();  //comment out if necessary
				}
				//Log.d(TAG, "Value of the element is " + configXml.getText());
				eventType = configXml.getEventType();
			}
		} catch (XmlPullParserException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		return tests;
	}
	
	/**
	 * Read the scores config file from internal storage and populate the test object score values
	 */
	protected void readScoreXmlFile() {
		Log.d(TAG, "Entering readScoreXmlFile() method...");
		try {

			File myFile = new File(getActivity().getFilesDir(), "scores.txt");
			FileInputStream fIn = new FileInputStream(myFile);
			BufferedReader myReader = new BufferedReader(
					new InputStreamReader(fIn));
			String aTestScoreRow = "";
			//String aBuffer = "";
			int index = 0;
			while ((aTestScoreRow = myReader.readLine()) != null) {
				Log.d(TAG, "The test score is: " + aTestScoreRow);
				String[] pair = aTestScoreRow.split("=");
				//String key = pair[0];
				String value = pair[1];
				// populate test object score values
				if(!value.equals("-1")) {
					testObjects.get(index).setScore(Integer.parseInt(value));
					testObjects.get(index).setScoreText("Obtained score: " + value + "/10 >");
				} else {
					testObjects.get(index).setScoreText("Try me out! >");
				}
				index++;
			}
			myReader.close();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

}
