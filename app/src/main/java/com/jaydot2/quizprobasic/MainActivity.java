package com.jaydot2.quizprobasic;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.content.res.XmlResourceParser;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jaydot2.quizprobasic.model.Question;
import com.jaydot2.quizprobasic.model.TestObject;
import com.jaydot2.quizprobasic.ResultsFragment.OnResultsListener;
import com.jaydot2.quizprobasic.TestListFragment.OnTestSelectedListener;
import com.jaydot2.quizprobasic.TestQuestionFragment.OnTestListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity implements OnTestSelectedListener, OnTestListener, OnResultsListener {

    private FragmentManager fragmentManager;
    private Fragment mTestListFragment;
    //private Fragment mResultsFragment;
    private TestQuestionFragment testQuestionFragment;
    private ArrayList<TestObject> testObjectList;

    protected final static String DEFAULT_Z = "Z";

    private File scoreFile;

    /**
     * Index of test for which results are shown
     */
    private int mTestResultIndex = -1;

    private final static String TAG = "MainActivity";
    public final static String TEST_SOLUTION = "solution";
    public final static String TESTS = "tests";
    public final static String TEST = "test";
    public final static String TEST_TITLE = "test_title";
    public final static String REVIEW_LIST_KEY = "review";
    public final static String NUM_CORRECT_KEY = "correct";
    private final static int MAX_NUMBER_OF_QUESTIONS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        // 1. Populate the test data from the config.xml file
        testObjectList = loadAndParseLocalXml();

        // 2. populate the solution text from the solutions.xml file
        loadAndParseSolutionsXml(testObjectList);

        // 3. Make sure there is a scores.txt config file
        scoreFile = new File(getFilesDir(), "scores.txt");
        if(!scoreFile.exists()) {
            writeScoreXml(testObjectList);
        }

        // 4. Populate the score properties in the TestOjects
        readScoreXmlFile();

        fragmentManager = getFragmentManager();
        mTestListFragment = fragmentManager.findFragmentById(R.id.test_list_fragment);

        if (savedInstanceState == null) {
            if(mTestListFragment == null) {
                mTestListFragment = new TestListFragment();
            }
            fragmentManager.beginTransaction().add(R.id.fragmentContainer, mTestListFragment).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_help) {
            //Toast.makeText(this, "app help", Toast.LENGTH_SHORT).show();
            openHelp();
            return true;
        }
        if (id == R.id.action_quit) {
            quitApp(this);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onTestSelected(int index) {
        ArrayList<String> testList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.tests)));
        //Toast.makeText(this, "Test selected:  " + testList.get(index), Toast.LENGTH_SHORT).show();
        if(fragmentManager == null) {
            fragmentManager = getFragmentManager();
        }

        testQuestionFragment = new TestQuestionFragment();

        Bundle arguments = new Bundle();
        TestObject to = testObjectList.get(index);

        // make sure question selected option values are set to DEFAULT_Z to prevent null pointer error
        for(Question q : to.getQuestions()) {
            q.setSelectedOption(DEFAULT_Z);
        }

        arguments.putInt("index", index);
        arguments.putSerializable(TEST, to);
        testQuestionFragment.setArguments(arguments);

        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, testQuestionFragment).commit();

        mTestResultIndex = index;
        setTitle(testList.get(index));
    }


    @Override
    public void onEndTestClick(int _numCorrect, String _review, String _title) {
		/*
		if(fragmentManager == null) {
			fragmentManager = getFragmentManager();
		}
		if(mResultsFragment == null) {
			mResultsFragment = new ResultsFragment();
    	}
		*/

        if(mTestResultIndex < 0) {
            mTestResultIndex = 0;
        }

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
	    /* dialog, so make our own transaction and take care of that here.
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);*/


        Bundle arguments = new Bundle();
        arguments.putInt(NUM_CORRECT_KEY, _numCorrect);
        arguments.putString(REVIEW_LIST_KEY, _review);
        arguments.putString(TEST_TITLE, _title);

        testObjectList.get(mTestResultIndex).setScore(_numCorrect);
        testObjectList.get(mTestResultIndex).setScoreText("score: " + _numCorrect + " >");
        writeScoreXml(testObjectList);

        DialogFragment resultsDialogFragment = ResultsDialogFragment.newInstance(_numCorrect, _review, _title);
        //resultsDialogFragment.setArguments(arguments);
        resultsDialogFragment.show(fragmentManager, "dialog");
    }

    @Override
    public void onShowSoluton(String _solutionText) {
        openSolutionDialog(_solutionText);
    }

    /**
     * Go to screen to select a new test
     */
    @Override
    public void onShowTestList() {
        if(fragmentManager == null) {
            fragmentManager = getFragmentManager();
        }
        if(mTestListFragment == null) {
            mTestListFragment = new TestListFragment();
        }
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, mTestListFragment).commit();
        setTitle(getResources().getString(R.string.tests_label));
    }

    private void openHelp() {
        openHelpDialog();
    }

    private void quitApp(Activity activity) {
        activity.finish();
    }

    /**
     *
     */
    private void openHelpDialog() {
        DialogFragment dmHelp = new HelpDialogFragment();
        dmHelp.show(getFragmentManager(), getResources().getString(R.string.action_help));

    }

    private void openSolutionDialog(String _solutionText) {
        Bundle arguments = new Bundle();
        arguments.putString(TEST_SOLUTION, _solutionText);
        DialogFragment dmSolution = SolutionDialogFragment.newInstance(_solutionText);
        dmSolution.show(getFragmentManager(), "sdialog");
    }

    /**
     * <pre>
     * Populate the list of TestObjects from data in the config.xml file
     * </pre>
     * @return
     */
    private ArrayList<TestObject> loadAndParseLocalXml() {
        Log.d(TAG, "loadAndParseLocalXml");
        ArrayList<TestObject> tests = new ArrayList<TestObject>();
        XmlResourceParser configXml = getResources().getXml(R.xml.config);
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
                        TestObject to = new TestObject();
                        List<Question> questions = new ArrayList<Question>();
                        Log.d(TAG, "ID of the test is " + configXml.getAttributeValue(0));
                        Log.d(TAG, "Title of the test is " + configXml.getAttributeValue(1));
                        to.setTestID(configXml.getAttributeValue(0));
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
     * Read the solutions XML file and populate the solutions with
     */
    protected void loadAndParseSolutionsXml(ArrayList<TestObject> _tests) {
        Log.d(TAG, "loadAndParseSolutionsXml");
        XmlResourceParser solutionsXml = getResources().getXml(R.xml.solutions);
        //String testID = "";
        int _testIndex = 0;
        try {
            Log.d(TAG, "Start parsing XML...");
            int eventType = solutionsXml.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG) {
                    String element = solutionsXml.getName();
                    Log.d(TAG, "XML element is " + element);
                    if(element.equals("test")) {

                        Log.d(TAG, "ID of the test is " + solutionsXml.getAttributeValue(0));
                        Log.d(TAG, "Title of the test is " + solutionsXml.getAttributeValue(1));

                        for(int i = 0; i < MAX_NUMBER_OF_QUESTIONS; i++) {
                            solutionsXml.next();
                            if((solutionsXml.getName() != null) && (solutionsXml.getName().equals("question"))) {
                                //String _solution =  solutionsXml.getAttributeValue(1);
                                String _strIndex = solutionsXml.getAttributeValue(0);
                                int _index = Integer.parseInt(_strIndex) - 1;

                                solutionsXml.next();  // go to node after question node
                                String _solutionText = solutionsXml.getText().toString();
                                testObjectList.get(_testIndex).getQuestions().get(_index).setSolutionText(_solutionText);
                                Log.d(TAG, "The solution text is: " + _solutionText);
                                solutionsXml.next();
                            }
                        }
                        solutionsXml.next();
                        Log.d(TAG, "The current text index is " + _testIndex);
                        _testIndex++;
                    }
                }
                if(eventType != XmlPullParser.END_DOCUMENT) {
                    solutionsXml.next();  //comment out if necessary
                }
                eventType = solutionsXml.getEventType();
            }
        } catch (XmlPullParserException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test method  testObjectList
     */
    protected void writeScoreXml() {
        try {
            // use internal storage
            File myFile = new File(getFilesDir(), "scores.txt");
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter scoresOutWriter = new OutputStreamWriter(fOut);
            scoresOutWriter.append("\ntest1=8");
            scoresOutWriter.append("\ntest2=9");
            scoresOutWriter.append("\ntest3=5");
            scoresOutWriter.append("\ntest4=-1");
            scoresOutWriter.append("\ntest5=5");
            scoresOutWriter.append("\ntest6=5");
            scoresOutWriter.append("\ntest7=5");
            scoresOutWriter.close();
            fOut.close();
            //Toast.makeText(getBaseContext(), "Done writing SD 'scores.txt'", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"Done writing SD 'scores.txt'");
        } catch(IOException ioe) {
            Log.e(TAG, ioe.getMessage());
        }
    }

    /**
     * Get a list of TestObjects and extract the testID and score values.<br/>
     * Write these values to a scores configuration file in the app's internal storage
     *
     * @param _tests
     * Test method  testObjectList
     */
    private void writeScoreXml(List<TestObject> _tests) {
        Log.d(TAG, "Entering writeScoreXml(List) method...");
        try {
            // use internal storage
            scoreFile = new File(getFilesDir(), "scores.txt");
            scoreFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(scoreFile);
            OutputStreamWriter scoresOutWriter = new OutputStreamWriter(fOut);
            // iterate over test objects and write to scores config file
            for(TestObject _test : _tests) {
                scoresOutWriter.append(_test.getTestID() + "=" + _test.getScore() + "\n");
            }
            scoresOutWriter.close();
            fOut.close();
            Log.d(TAG, "Done writing SD 'scores.txt'");
        } catch(IOException ioe) {
            Log.e(TAG, ioe.getMessage());
        }
    }

    /**
     * Read the scores config file from internal storage and populate the test object score values
     */
    protected void readScoreXmlFile() {
        Log.d(TAG, "Entering readScoreXmlFile() method...");
        try {

            File myFile = new File(getFilesDir(), "scores.txt");
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
                    testObjectList.get(index).setScore(Integer.parseInt(value));
                    testObjectList.get(index).setScoreText("score: " + value + " >");
                }
                index++;
            }
            myReader.close();
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void doPositiveClick() {
        onShowTestList();
        Log.i(TAG, "Positive click!");
    }

    public void doNegativeClick() {

        Log.i(TAG, "Negative click!");
    }
}
