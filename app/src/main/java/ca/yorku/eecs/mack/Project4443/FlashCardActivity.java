package ca.yorku.eecs.mack.Project4443;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class FlashCardActivity extends Activity implements View.OnTouchListener{
    public final static String QUIZ_LENGTH_KEY = "number_of_questions"; //Used in QuizActivity
    public final static String QUIZ_MODE = "haptic and auditory";

    private final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    // Options menu items (used for groupId and itemId)
    private final static int QUIZ = 0;
    private final static int SETTINGS = 1;
    private final static int ADD = 2;
    private final static int DELETE = 3;

    private final static String SHOWING_BACK_KEY = "showing_back";
    private final static String CARD_INDEX_KEY = "card_index";
    private final static String BACK_STACK_KEY = "back_stack";
    private final static String WORD_KEY = "words";
    private final static String NUMBER_OF_QUESTIONS_KEY = "1";

    ArrayList<Integer> myBackStack;
    static ArrayList<Flashcard> flashcards;
    static int cardIdx;

    FrameLayout cardView; // we need these to attach the touch listeners
    GestureDetector gestureDetector; // use Android's GestureDetector for touch gestures
    Vibrator vib; // vibrate when the dialog pops up
    SharedPreferences sp;
    boolean showingBack; // true = showing back of card
    boolean toastBeforeExit;
    boolean hapticandauditorymode;

    int numberOfQuestions;
    File file;
    ArrayList<String> words;
    ArrayList<String> backup;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        toastBeforeExit = true; // the user gets one reminder before exiting app

        // UI responds to touch gestures
        cardView = (FrameLayout)findViewById(R.id.container);
        cardView.setOnTouchListener(this);
        gestureDetector = new GestureDetector(this.getBaseContext(), new MyGestureListener());

        // init vibrator (used for long-press gesture)
        vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        //make file dir is dose not exist
        File dir = new File(FlashCardActivity.this.getFilesDir(), "text");
        if (!dir.exists()) {
            dir.mkdir();
        }
        //read file
        file = new File(FlashCardActivity.this.getFilesDir() + "/text/cards.txt");

        if (savedInstanceState == null) // the activity is being created for the 1st time
        {
            // initialize SharedPreferences instance and load the settings
            sp = PreferenceManager.getDefaultSharedPreferences(this);
            loadSettings();

            // load the flash card (and related info) from the string array resource
            //String[] flashCardArray = getResources().getStringArray(R.array.flashcard);
            flashcards = new ArrayList<Flashcard>();
            backup = new ArrayList<String>();
            readFile();
            Log.i(MYDEBUG, "Number of flashcards = " + flashcards.size());

            // build a string of words (used in showNamesListDialog)
            words = new ArrayList<String>();
            for (int i = 0; i < flashcards.size(); ++i)
                words.add(flashcards.get(i).word);

            // a simple backstack (just store the indices of the quotations visited)
            myBackStack = new ArrayList<Integer>();
            cardIdx = 0;

            // put the fragments UIs in the activity's content
            getFragmentManager().beginTransaction().add(R.id.container, new WordFragment()).commit();
        }
    }

    private void loadSettings()
    {
        // build the keys (makes the code more readable)
        final String PREF_QUIZ_LENGTH_KEY = getBaseContext().getString(R.string.pref_quiz_length_key);

        numberOfQuestions = Integer.parseInt(sp.getString(PREF_QUIZ_LENGTH_KEY, "1"));
        hapticandauditorymode = sp.getBoolean(QUIZ_MODE, false);
    }

    // Setup an Options menu (used for Quiz, etc.).
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(0, QUIZ, QUIZ, R.string.menu_quiz);
        menu.add(0, SETTINGS, SETTINGS, R.string.menu_settings);
        menu.add(0, ADD, ADD, R.string.menu_add);
        menu.add(0, DELETE, DELETE, R.string.menu_delete);
        return true;
    }

    // Handle an Options menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case QUIZ:
                if(flashcards.size() == 4){
                    Toast.makeText(this, "You need at least 4 flashcard to start the quiz", Toast.LENGTH_LONG).show();
                    break;
                }else if(flashcards.size() < numberOfQuestions){
                    Toast.makeText(this, "You need at least " +numberOfQuestions +" flashcards to start the quiz", Toast.LENGTH_LONG).show();
                    break;
            }
                final Bundle b = new Bundle();
                b.putInt(QUIZ_LENGTH_KEY, numberOfQuestions);
                b.putBoolean(QUIZ_MODE, hapticandauditorymode);

                Intent quizIntent = new Intent(getApplicationContext(), QuizActivity.class);
                quizIntent.putExtras(b);
                startActivityForResult(quizIntent, QUIZ);
                return true;

            case SETTINGS:
                // launch the SettingsActivity to allow the user to change the app's settings
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(i, SETTINGS);
                break;

            case ADD:
                Intent add = new Intent(getApplicationContext(), AddCard.class);
                startActivityForResult(add, ADD);
                break;
            case DELETE:
                removeCard();
                break;
        }
        return false;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == SETTINGS)
        {
            sp = PreferenceManager.getDefaultSharedPreferences(this);
            loadSettings();
        }
        if (requestCode == ADD){
            String newWord = data.getStringExtra("word");
            String newDef = data.getStringExtra("def");
            String s = newWord + "#"+newDef;
            addCard(s);
            backup.add(s);
            flashcards.add(new Flashcard(newWord,newDef));
            words.add(newWord);
            cardIdx = flashcards.size()-2;
            nextWord();
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent me)
    {
        // let the gesture detector process the touch event (see MyGestureListener below)
        gestureDetector.onTouchEvent(me);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        int n = myBackStack.size();
        if (n > 0)
        {
            cardIdx = myBackStack.remove(n - 1);

            showingBack = false;
            getFragmentManager().beginTransaction().setCustomAnimations(R.animator.view_appear_enter,
                    R.animator.view_appear_exit).replace(R.id.container, new WordFragment()).commit();
            return;
        } else if (toastBeforeExit)
        {
            toastBeforeExit = false;
            Toast.makeText(this, "Press Back once more to exit", Toast.LENGTH_LONG).show();
            return;
        }
        finish();
    }
    private void flipCard()
    {
        /*
         * Create and commit a new fragment transaction to replace the current fragment. The
         * transition includes a "card flip" animation.
         */
        if (showingBack)
        {
            // getFragmentManager().popBackStack();
            getFragmentManager().beginTransaction().setCustomAnimations(R.animator.view_flip_enter,
                    R.animator.view_flip_exit).replace(R.id.container, new WordFragment()).commit();
        } else
        {
            getFragmentManager().beginTransaction().setCustomAnimations(R.animator.view_flip_enter,
                    R.animator.view_flip_exit).replace(R.id.container, new DefinitionFragment()).commit();
        }
        showingBack = !showingBack; // toggle showing back/front
    }
    // restore state variables after a screen rotation
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        flashcards = (ArrayList<Flashcard>) getLastNonConfigurationInstance(); // see onRetainConfigurationInstance
        cardIdx = savedInstanceState.getInt(CARD_INDEX_KEY);
        showingBack = savedInstanceState.getBoolean(SHOWING_BACK_KEY);
        myBackStack = savedInstanceState.getIntegerArrayList(BACK_STACK_KEY);
        words = savedInstanceState.getStringArrayList(WORD_KEY);
        hapticandauditorymode = savedInstanceState.getBoolean(QUIZ_MODE);
        numberOfQuestions = savedInstanceState.getInt(NUMBER_OF_QUESTIONS_KEY);
    }
    // save state variables in the event of a screen rotation
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putInt(CARD_INDEX_KEY, cardIdx);
        savedInstanceState.putBoolean(SHOWING_BACK_KEY, showingBack);
        savedInstanceState.putIntegerArrayList(BACK_STACK_KEY, myBackStack);
        savedInstanceState.putStringArrayList(WORD_KEY, words);
        savedInstanceState.putBoolean(QUIZ_MODE, hapticandauditorymode);
        savedInstanceState.putInt(NUMBER_OF_QUESTIONS_KEY, numberOfQuestions);
        super.onSaveInstanceState(savedInstanceState);
    }

    public static class WordFragment extends Fragment
    {
        View frontView;
        TextView wordView;

        public WordFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            frontView = inflater.inflate(R.layout.word, container, false);
            wordView = (TextView)frontView.findViewById(R.id.word);
            if (flashcards.isEmpty())
                wordView.setText("Please add your flashcard!");
            else
                wordView.setText(flashcards.get(cardIdx).word);
            return frontView;
        }
    }

    public static class DefinitionFragment extends Fragment // implements View.OnTouchListener
    {
        View backView;
        TextView DefinitionView;

        public DefinitionFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            backView = inflater.inflate(R.layout.definition, container, false);
            DefinitionView = (TextView)backView.findViewById(R.id.definition);
            if (flashcards.isEmpty())
                DefinitionView.setText("Please add your flashcard!");
            else
                DefinitionView.setText(flashcards.get(cardIdx).definition);
            return backView;
        }
    }
    private void showCardListDialog() {
        AlertDialog.Builder parameters = new AlertDialog.Builder(this);
        String[] list = words.toArray(new String[words.size()]);
        parameters.setCancelable(true).setTitle(R.string.menu_title).setItems(list,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        myBackStack.add(cardIdx); // idx of quote that *was* showing
                        cardIdx = which; // new Idx
                        showingBack = false;

                        getFragmentManager().beginTransaction().setCustomAnimations(R.animator.view_appear_enter,
                                R.animator.view_appear_exit).replace(R.id.container, new WordFragment()).commit();
                    }
                }).show();
    }
    private void nextWord() {
        myBackStack.add(cardIdx);
        if(cardIdx != flashcards.size() -1)
            cardIdx++;
        else
            cardIdx = 0;
        showingBack = false;
        getFragmentManager().beginTransaction().setCustomAnimations(R.animator.view_appear_enter,
                        R.animator.view_appear_exit).replace(R.id.container, new WordFragment()).addToBackStack(null)
                .commit();
    }
    private void addCard(String newWord){
        if (newWord != "") {
            try {
                FileWriter writer = new FileWriter(file,true);
                writer.append(newWord);
                writer.append("\n");
                writer.flush();
                writer.close();
                Toast.makeText(FlashCardActivity.this, "Saved your flashcard", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
            }
        }
    }
    private void removeCard(){
        flashcards.remove(cardIdx);
        words.remove(cardIdx);
        backup.remove(cardIdx);
        cardIdx--;

        if (file.exists()) {
            if (file.delete()) {
                System.out.println("file Deleted");
                try {
                    FileWriter writer = new FileWriter(file,true);
                    for (int i = 0; i < backup.size(); i++){
                        writer.append(backup.get(i));
                        writer.append("\n");
                        writer.flush();
                    }
                    writer.close();
                    Toast.makeText(FlashCardActivity.this, "Saved your flashcard", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                }
            } else {
                System.out.println("file not Deleted");
            }
        }
        nextWord();
    }
    private void readFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] s = line.split("#");
                flashcards.add(new Flashcard(s[0], s[1]));
                backup.add(line);
            }
            br.close();
        } catch (IOException e) { }
    }
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent me) {
            nextWord();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent me) {
            vib.vibrate(50);
            showCardListDialog(); // pop up a list of flash card
        }

        @Override
        public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
            flipCard(); // flip the image/bio card
            return true;
        }
    }
}
