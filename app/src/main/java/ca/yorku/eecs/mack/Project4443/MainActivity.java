package ca.yorku.eecs.mack.Project4443;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {

    public final static String QUIZ_LENGTH_KEY = "number_of_questions"; //Used in QuizActivity
    public final static String QUIZ_MODE = "haptic and auditory";
    private final static String MYDEBUG = "MYDEBUG"; // for Log.i messages
    private final static String WORD_KEY = "words";
    private final static String DEF_KEY = "defs";
    private final static String NUMBER_OF_QUESTIONS_KEY = "1";
    private final static int SETTINGS = 1;// Options menu items (used for groupId and itemId)

    static ArrayList<String> words,defs,backup;
    static File file;

    Button add,viewCard,quiz;
    SharedPreferences sp;
    boolean toastBeforeExit;
    boolean hapticandauditorymode;

    int numberOfQuestions;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        toastBeforeExit = true; // the user gets one reminder before exiting app

        add = (Button)findViewById(R.id.AddCardButton);
        viewCard = (Button)findViewById(R.id.ViewCardButton);
        quiz = (Button)findViewById(R.id.StartQuizButton);
        add.setOnClickListener(this);
        viewCard.setOnClickListener(this);
        quiz.setOnClickListener(this);

        //make file dir is dose not exist
        File dir = new File(MainActivity.this.getFilesDir(), "carddecks");
        if (!dir.exists()) {
            dir.mkdir();
        }
        if (savedInstanceState == null) // the activity is being created for the 1st time
        {
            // initialize SharedPreferences instance and load the settings
            sp = PreferenceManager.getDefaultSharedPreferences(this);
            loadSettings();
            Log.i(MYDEBUG, "Number of flashcards = " + words.size());
        }
    }
    // Setup an Options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(0, SETTINGS, SETTINGS, R.string.menu_settings);
        return true;
    }
    // Handle an Options menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // launch the SettingsActivity to allow the user to change the app's settings
        Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivityForResult(i, SETTINGS);
        return false;
    }
    private void loadSettings()
    {
        // build the keys (makes the code more readable)
        final String PREF_QUIZ_LENGTH_KEY = getBaseContext().getString(R.string.pref_quiz_length_key);

        numberOfQuestions = Integer.parseInt(sp.getString(PREF_QUIZ_LENGTH_KEY, "1"));
        hapticandauditorymode = sp.getBoolean(QUIZ_MODE, false);
        readFile();//read file
    }

    private void readFile() {
        if(hapticandauditorymode){//if true, i know use is in second trial mode, read the second list
            file = new File(MainActivity.this.getFilesDir() + "/carddecks/cards2.txt");
        }
        else
            file = new File(MainActivity.this.getFilesDir() + "/carddecks/cards1.txt");

        try {
            words = new ArrayList<String>();
            defs = new ArrayList<String>();
            backup = new ArrayList<String>();
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));//read file
                String line;

                while ((line = br.readLine()) != null) {
                    String[] s = line.split("#");
                    words.add(s[0]);//save words
                    defs.add(s[1]);//save definitions
                    backup.add(line);//save line
                }
                br.close();
            }
        } catch (IOException e) { }
    }
    @Override
    public void onClick(View view) {
        if (view == add) {
            Intent i = new Intent(getApplicationContext(), AddCard.class);
            startActivity(i);
        }
        else if(view == viewCard){
            if (!words.isEmpty()){
                Intent i = new Intent(getApplicationContext(), SearchableActivity.class);
                startActivity(i);
            }
            else
                Toast.makeText(this, "Your card deck is currently empty!", Toast.LENGTH_LONG).show();

        }
        else if(view == quiz){
            if(words.isEmpty()){
                Toast.makeText(this, "You need at least 1 flashcard to start the quiz", Toast.LENGTH_LONG).show();
            }else if(words.size() < numberOfQuestions){
                Toast.makeText(this, "You need at least " +numberOfQuestions +" flashcards to start the quiz", Toast.LENGTH_LONG).show();
            }
            else{
                final Bundle b = new Bundle();
                b.putInt(QUIZ_LENGTH_KEY, numberOfQuestions);
                b.putBoolean(QUIZ_MODE, hapticandauditorymode);

                Intent quizIntent = new Intent(getApplicationContext(), QuizActivity.class);
                quizIntent.putExtras(b);
                quizIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(quizIntent);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == SETTINGS)
        {
            sp = PreferenceManager.getDefaultSharedPreferences(this);
            loadSettings();
        }
    }
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        words = savedInstanceState.getStringArrayList(WORD_KEY);
        defs = savedInstanceState.getStringArrayList(DEF_KEY);
        hapticandauditorymode = savedInstanceState.getBoolean(QUIZ_MODE);
        numberOfQuestions = savedInstanceState.getInt(NUMBER_OF_QUESTIONS_KEY);
    }

    // save state variables in the event of a screen rotation
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putStringArrayList(WORD_KEY, words);
        savedInstanceState.putStringArrayList(DEF_KEY, defs);
        savedInstanceState.putBoolean(QUIZ_MODE, hapticandauditorymode);
        savedInstanceState.putInt(NUMBER_OF_QUESTIONS_KEY, numberOfQuestions);
        super.onSaveInstanceState(savedInstanceState);
    }
}
