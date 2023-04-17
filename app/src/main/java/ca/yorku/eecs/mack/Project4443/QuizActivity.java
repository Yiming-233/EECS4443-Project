package ca.yorku.eecs.mack.Project4443;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Random;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
 * The activity to implement the quiz.  
 */

public class QuizActivity extends Activity implements View.OnTouchListener, ResultsDialog.OnResultsDialogClickListener
{
	//private final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

	// keys for get/put methods
	private final static String QUESTION_INDEX_KEY = "question_index";
	private final static String START_TIME_KEY = "start_time";
	private final static String FIRST_ANSWER_FLAG_KEY = "first_answer_flag";
	
	// the following are public because they are also used in ResultsDialog
	public final static String NUMBER_CORRECT_KEY = "number_correct"; 
	public final static String NUMBER_INCORRECT_KEY = "number_incorrect";
	public final static String COMPLIATION_TIME_KEY = "compilation_time";
	private final static int NUMBER_OF_ANSWERS = 4; // ... for each question in the quiz

	long startTime, elapsedTime;
	static int numberCorrect, numberIncorrect;
	boolean firstAnswerFlag, attempted;
	String timeString;

	GestureDetector gestureDetector; // use Android's GestureDetector for touch gestures
	FrameLayout bottomView; // we need these to attach the touch listeners
	Vibrator vib;
	MediaPlayer click, blip, miss;
	File file;

	/*
	 * These variables are static because they are referenced in the Fragments used to create the UI
	 * for the quiz. The Fragments themselves are static, as required. See...
	 * 
	 * http://developer.android.com/guide/components/fragments.html
	 */
	static int numberOfQuestions; // a Setting passed in via onCreate bundle
	static boolean hapticandauditorymode; // Setting(passed in via onCreate bundle
	static Question[] q;
	static int questionIdx;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_quiz);

		bottomView = (FrameLayout)findViewById(R.id.quiz_container2);
		bottomView.setOnTouchListener(this);

		if (savedInstanceState == null) // the activity is being created for the 1st time
		{
			// data passed from the calling activity in startActivityForResult
			Bundle b = getIntent().getExtras();
			numberOfQuestions = b.getInt(MainActivity.QUIZ_LENGTH_KEY, 1);
			hapticandauditorymode = b.getBoolean(MainActivity.QUIZ_MODE, false);

			// init vibrator (used for incorrect answer, if enabled)
			if (hapticandauditorymode){
				vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
			}


			/*
			 * Initializing the MediaPlayer object is simple enough (see below). However, calls to
			 * "start" sometimes have no effect -- no audio is heard. This is probably because the
			 * MediaPlayer is in a state that precludes (re)starting the audio. Unfortunately, the
			 * API, API Guides, and StackOverflow provide very little insight.
			 * 
			 * The fix seems to be creating a fresh instance of the MediaPlayer immediately before
			 * calling start. This works fine, so the following lines are commented-out. They remain
			 * simply to draw attention to this discussion.
			 */
			// click = MediaPlayer.create(getBaseContext(), R.raw.click);
			// blip = MediaPlayer.create(getBaseContext(), R.raw.blip);
			// miss = MediaPlayer.create(getBaseContext(), R.raw.miss);

			// load the Quotations array
			/*
			 * Creating the questions for the quiz is two-step process:
			 * 
			 * First, we create an array of the indices of the quotations to use for the quiz
			 * questions. This is a bit tricky because each question in the quiz must be unique.
			 */
			int[] temp; // an array of unique indices for the questions in the quiz

			file = new File(QuizActivity.this.getFilesDir() + "/carddecks/question.txt");
			if(!retriveQuestion()){//if false, i know it is first attempt，genarate question
				attempted = false;
				q = new Question[numberOfQuestions];
				temp = randomArray(numberOfQuestions, MainActivity.words.size());
				//disabled for testing
//				do
//				{
//					temp = randomArray(numberOfQuestions, MainActivity.words.size());
//				} while (repeats(temp)); // Note: repeats(temp) will return false most of the time

				for (int i = 0; i < q.length; ++i)
					q[i] = new Question(temp[i], NUMBER_OF_ANSWERS);
			}


			startTime = System.nanoTime(); // the time when the quiz started

			/*
			 * The following initializations are not necessary, since the values are the default
			 * values. We're including this mostly as reminder of the variables that we need to
			 * think about when there is a configuration change due to a screen rotation. See
			 * onSaveInstanceState and onRestoreInstanceState.
			 */
			// questionCount = 0;
			elapsedTime = 0;
			numberCorrect = 0;
			numberIncorrect = 0;
			firstAnswerFlag = false;
			questionIdx = 0;

			// create the UI for the quiz
			getFragmentManager().beginTransaction().add(R.id.quiz_container2,
					new QuizQuestionFragment()).commit();
			getFragmentManager().beginTransaction().add(R.id.quiz_container1, new QuizAnswersFragment()).commit();
		}
	}

	// return an array of random integers in the specified range
	private int[] randomArray(int arraySize, int range)
	{
		int[] array = new int[arraySize];
		Random r = new Random();
		for (int i = 0; i < array.length; ++i)
			array[i] = r.nextInt(range);
		return array;
	}

	// return true if there are any repeated values in an array
	private boolean repeats(int[] array)
	{
		for (int i = 0; i < array.length; ++i)
			for (int j = i + 1; j < array.length; ++j)
				if (array[i] == array[j])
					return true;
		return false;
	}

	@Override
	public void onStop()
	{
		/*
		 * Release the audio resources. We do this in onStop, as suggested in the MediaPlayer API
		 * Guide. See...
		 * 
		 * http://developer.android.com/guide/topics/media/mediaplayer.html#releaseplayer
		 */
		if (click != null)
		{
			click.release();
			click = null;
		}
		if (blip != null)
		{

			blip.release();
			blip = null;
		}
		if (miss != null)
		{
			miss.release();
			miss = null;
		}
		super.onStop();
	}

	@Override
	public boolean onTouch(View v, MotionEvent me)
	{
		// let the gesture detector process the touch event (see MyGestureListener below)
		gestureDetector.onTouchEvent(me);
		return true;
	}

	/*
	 * The user's answer is processed here. The appropriate dialog is popped up depending on whether
	 * the answer is correct or incorrect.
	 */
	public void buttonClick(View v)
	{
		String correctAnswer = MainActivity.words.get(q[questionIdx].cardIdx);

		Drawable originalBackground = v.getBackground();
		ColorDrawable redBackground = new ColorDrawable(Color.RED);

		if (((Button)v).getText().equals(correctAnswer)) // correct answer
		{
			if (hapticandauditorymode)
			{
				// MediaPlayer instance created here (see comment in onCreate)
				click = MediaPlayer.create(getBaseContext(), R.raw.click);
				click.start();
			}
			if (!firstAnswerFlag)
			{
				++numberCorrect;
				firstAnswerFlag = true;
			}
			showCorrectDialog();
		} else
		// wrong answer
		{
			v.setBackground(redBackground);
			if (hapticandauditorymode)
			{
				// MediaPlayer instance created here (see comment in onCreate)
				miss = MediaPlayer.create(getBaseContext(), R.raw.miss);
				miss.start();
			}
			if (hapticandauditorymode)
				vib.vibrate(50);
			if (!firstAnswerFlag)
			{
				++numberIncorrect;
				firstAnswerFlag = true;
			}
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					v.setBackground(originalBackground);
				}
			}, 1000);
			//showIncorrectDialog();
		}

	}

	/*
	 * Get the next question for the quiz. Before doing this, increment the question count and check
	 * if we are done. If we are done, compute the elapsed time, output some audio (if enabled), and
	 * pop up a results dialog. The user's response to the results dialog will terminate the quiz
	 * activity and pass control back to the main activity.
	 */
	private void nextQuestion()
	{
		++questionIdx;
		if (questionIdx == numberOfQuestions)
		{
			// The quiz is done! Stop the clock (figuratively speaking) and build the time string
			elapsedTime = System.nanoTime() - startTime;
			timeString = String.format(Locale.getDefault(), "%1.1f sec", (elapsedTime / 1000000000f));

			if (hapticandauditorymode) // as per Settings in Options Menu
			{
				// MediaPlayer instance created here (see comment in onCreate)
				blip = MediaPlayer.create(getBaseContext(), R.raw.blip);
				blip.start();
			}
			showResultsDialog(); // ... and then finish (see onClick in showResultsDialog)

		} else
		{
			firstAnswerFlag = false;
			getFragmentManager().beginTransaction().replace(R.id.quiz_container2, new QuizQuestionFragment()).commit();
			getFragmentManager().beginTransaction().replace(R.id.quiz_container1, new QuizAnswersFragment()).commit();
		}
	}

	/*
	 * The dialog that pops up to show results at the end of a quiz.
	 * 
	 * This dialog is a bit more involved because we want to pass data in to the dialog to display.
	 * We're using a custom layout (res/layout/results_dialog.xml) and defining the dialog as a
	 * DialogFragment in a separate file (ResultsDialog.java). Consult for details. The approach
	 * here closely follows the recommendations in the DialogFragment API. See...
	 * 
	 * http://developer.android.com/reference/android/app/DialogFragment.html#AlertDialog
	 */
	private void showResultsDialog()
	{
		// Create an instance of the dialog fragment (passing in the data we want to show)
		DialogFragment df = ResultsDialog.newInstance(numberCorrect, numberIncorrect, timeString);

		// show the dialog
		df.show(getFragmentManager(), "ResultsDialogFragment");
	}

	@Override
	public void onResultsDialogClick(DialogFragment dialog)
	{
		// The user tapped the results dialog Continue button (this quiz is done)
		if (attempted)
			file.delete();
		else
			saveQuestion();

		this.finish();
	}

	public void saveQuestion(){
		String s;
		try {
			FileWriter writer = new FileWriter(file,true);
			for(int i = 0; i < q.length;i++){
				s = q[i].cardIdx + "#" + q[i].answerArray[0]+ "#" + q[i].answerArray[1]+ "#" + q[i].answerArray[2]+ "#" + q[i].answerArray[3];
				writer.append(s);
				writer.append("\n");
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
		}
	}
	public boolean retriveQuestion(){

		if (file.exists()) {
			try{
				BufferedReader br = new BufferedReader(new FileReader(file));//read file
				String line;
				String[] s;
				int i = 0;
				Question temp;
				int lines = (int) Files.lines(file.toPath()).count();
				q = new Question[lines];
				while ((line = br.readLine()) != null) {
					s = line.split("#");
					temp = new Question(Integer.parseInt(s[0]),NUMBER_OF_ANSWERS);
					for(int j = 1; j < s.length;j++)
						temp.answerArray[j-1] = s[j];
					q[i] = temp;
					i++;
				}
				br.close();
				numberOfQuestions = q.length;
				if(q.length > 1){
					Random r = new Random();
					for (int j = q.length - 1; j > 0; j--) {
						int index = r.nextInt(j + 1);
						Question temp2 = q[index];
						q[index] = q[j];
						q[j] = temp2;
					}
				}
				attempted = true;
				return attempted;
			}catch (IOException e) { }
		}
		return false;
	}
	// The dialog that pops up if the user taps the correct answer
	private void showCorrectDialog()
	{
		// Initialize the dialog
		AlertDialog.Builder parameters = new AlertDialog.Builder(this);
		parameters.setCancelable(false).setTitle(R.string.correct_title).setNeutralButton(R.string.continue_string,
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.cancel(); // close this dialog
						nextQuestion();
					}
				}).show();
	}

	/*
	 * We also want to save and restore the Questions array. Since this is an Object, we are using
	 * the method described in the API Guide (Topic: Retaining an Object During a Configuration
	 * Change). See...
	 * 
	 * http://developer.android.com/guide/topics/resources/runtime-changes.html
	 * 
	 * See, also, the discussion and code in Demo Ink.
	 */
	@Override
	public Object onRetainNonConfigurationInstance()
	{
		return q;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		questionIdx = savedInstanceState.getInt(QUESTION_INDEX_KEY);
		elapsedTime = savedInstanceState.getLong(COMPLIATION_TIME_KEY);
		startTime = savedInstanceState.getLong(START_TIME_KEY);
		numberCorrect = savedInstanceState.getInt(NUMBER_CORRECT_KEY);
		numberIncorrect = savedInstanceState.getInt(NUMBER_INCORRECT_KEY);
		firstAnswerFlag = savedInstanceState.getBoolean(FIRST_ANSWER_FLAG_KEY);
		numberOfQuestions = savedInstanceState.getInt(MainActivity.QUIZ_LENGTH_KEY);
		hapticandauditorymode = savedInstanceState.getBoolean(MainActivity.QUIZ_MODE, false);

		if (hapticandauditorymode)
			vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE); // for long-press

		getFragmentManager().beginTransaction().replace(R.id.quiz_container1, new QuizAnswersFragment()).commit();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt(QUESTION_INDEX_KEY, questionIdx);
		savedInstanceState.putLong(COMPLIATION_TIME_KEY, elapsedTime);
		savedInstanceState.putLong(START_TIME_KEY, startTime);
		savedInstanceState.putInt(NUMBER_CORRECT_KEY, numberCorrect);
		savedInstanceState.putInt(NUMBER_INCORRECT_KEY, numberIncorrect);
		savedInstanceState.putBoolean(FIRST_ANSWER_FLAG_KEY, firstAnswerFlag);
		savedInstanceState.putInt(MainActivity.QUIZ_LENGTH_KEY, numberOfQuestions);
		savedInstanceState.putBoolean(MainActivity.QUIZ_MODE, hapticandauditorymode);

	}

	// ==================================================================================================
	public static class QuizAnswersFragment extends Fragment
	{
		View quizStatusView;
		Button answer1View, answer2View, answer3View, answer4View;

		public QuizAnswersFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			quizStatusView = inflater.inflate(R.layout.quiz_answer, container, false);
			answer1View = (Button)quizStatusView.findViewById(R.id.quiz_answer_1);
			answer2View = (Button)quizStatusView.findViewById(R.id.quiz_answer_2);
			answer3View = (Button)quizStatusView.findViewById(R.id.quiz_answer_3);
			answer4View = (Button)quizStatusView.findViewById(R.id.quiz_answer_4);

			/*
			 * This adjustment is necessary in case the screen is rotated WHILE THE RESULTS DIALOG
			 * IS SHOWING. Without this, the app crashes with an array index out of bounds
			 * exception.
			 */
			if (questionIdx == numberOfQuestions)
				--questionIdx;

			answer1View.setText(q[questionIdx].answerArray[0]);
			answer2View.setText(q[questionIdx].answerArray[1]);
			answer3View.setText(q[questionIdx].answerArray[2]);
			answer4View.setText(q[questionIdx].answerArray[3]);

			return quizStatusView;
		}
	}

	// ==================================================================================================
	public static class QuizQuestionFragment extends Fragment
	{
		View quizQuestionView;
		TextView quoteView, questionView;

		public QuizQuestionFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			quizQuestionView = inflater.inflate(R.layout.quiz_question, container, false);
			quoteView = (TextView)quizQuestionView.findViewById(R.id.quiz_quote);
			questionView = (TextView)quizQuestionView.findViewById(R.id.quiz_question);

			/*
			 * This adjustment is necessary in case the screen is rotated WHILE THE RESULTS DIALOG
			 * IS SHOWING. Without this, the app crashes with an array index out of bounds
			 * exception.
			 */
			if (questionIdx == numberOfQuestions)
				--questionIdx;

			quoteView.setText(MainActivity.defs.get(q[questionIdx].cardIdx));
			questionView.setText(R.string.quiz_question);
			return quizQuestionView;
		}
	}
}
