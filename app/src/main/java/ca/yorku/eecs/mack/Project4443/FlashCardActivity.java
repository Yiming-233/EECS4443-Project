package ca.yorku.eecs.mack.Project4443;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import java.io.File;
import java.io.FileWriter;

public class FlashCardActivity extends Activity implements OnClickListener, OnTouchListener{
    private final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    // Options menu items (used for groupId and itemId)
    private final static int DELETE = 1;

    static int cardIdx;

    FrameLayout cardView; // we need these to attach the touch listeners
    GestureDetector gestureDetector; // use Android's GestureDetector for touch gestures
    Vibrator vib; // vibrate when the dialog pops up
    SharedPreferences sp;
    boolean showingBack; // true = showing back of card
    boolean toastBeforeExit;
    boolean buttonorGesture;
    ImageButton next, prev, home;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        if (savedInstanceState == null) // the activity is being created for the 1st time
        {
            // initialize SharedPreferences instance and load the settings
            sp = PreferenceManager.getDefaultSharedPreferences(this);
            cardIdx = b.getInt("index", 0);
            // put the fragments UIs in the activity's content
            getFragmentManager().beginTransaction().add(R.id.container, new WordFragment()).commit();
        }
        buttonorGesture = b.getBoolean(MainActivity.VIEW_MODE,false);//the user select to use button or gesture
        toastBeforeExit = true; // the user gets one reminder before exiting app
        if(buttonorGesture){
            setContentView(R.layout.flashcard2);
            next = (ImageButton) findViewById(R.id.next);
            prev = (ImageButton) findViewById(R.id.previous);
            home = (ImageButton) findViewById(R.id.home);
            next.setOnClickListener(this);
            prev.setOnClickListener(this);
            home.setOnClickListener(this);
        }
        else
            setContentView(R.layout.flashcard);
        // UI responds to touch gestures
        cardView = (FrameLayout)findViewById(R.id.container);
        cardView.setOnTouchListener(this);
        gestureDetector = new GestureDetector(this.getBaseContext(), new MyGestureListener());

        // init vibrator (used for long-press gesture)
        vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
    }
    // Setup an Options menu (used for Quiz, etc.).
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(0, DELETE, DELETE, R.string.menu_delete);
        return true;
    }
    // Handle an Options menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //remove current selected card
        removeCard();
        return false;
    }
    @Override
    public boolean onTouch(View v, MotionEvent me)
    {
        // let the gesture detector process the touch event (see MyGestureListener below)
        gestureDetector.onTouchEvent(me);
        return true;
    }
    //only functioning when user select to use button mode
    public void onClick(View v){
        if (v == next){
            nextWord();//see below
        }
        else if(v == prev){
            onBackPressed();//see below
        }
        else
            finish();//see below
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
    private void nextWord() {
        if(cardIdx < MainActivity.words.size() -1)
            cardIdx++; //go to next word
        else
            cardIdx = 0;//if current word is the last, return to begining
        //reset boolean
        showingBack = false;
        toastBeforeExit = true;
        //show card
        getFragmentManager().beginTransaction().setCustomAnimations(R.animator.view_appear_enter,
                        R.animator.view_appear_exit).replace(R.id.container, new WordFragment()).addToBackStack(null)
                .commit();
    }
    @Override
    public void onBackPressed()
    {
        if (cardIdx > 0)
        {
            cardIdx--;
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
        if(cardIdx == 0)
            finish();
    }
    private void removeCard(){
        //remove card from arraylist
        MainActivity.words.remove(cardIdx);
        MainActivity.defs.remove(cardIdx);
        MainActivity.backup.remove(cardIdx);
        cardIdx--;
        //read file
        File file = MainActivity.file;
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("file Deleted");
                try {
                    //rewrite file
                    FileWriter writer = new FileWriter(file,true);
                    for (int i = 0; i < MainActivity.backup.size(); i++){
                        writer.append(MainActivity.backup.get(i));
                        writer.append("\n");
                        writer.flush();
                    }
                    writer.close();
                    Toast.makeText(FlashCardActivity.this, "Flashcard is deleted", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                }
            } else {
                System.out.println("file not Deleted");
            }
        }
        //return to home page when no more card left
        if(MainActivity.words.isEmpty())
            finish();
        else //else move on to next card
            nextWord();
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
            wordView.setText(MainActivity.words.get(cardIdx));
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
            DefinitionView.setText(MainActivity.defs.get(cardIdx));
            return backView;
        }
    }
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent me) {
            if (!buttonorGesture)
                nextWord();
            return true;
        }
        @Override
        public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
            flipCard(); // flip the image/bio card
            return true;
        }
        @Override
        public void onLongPress(MotionEvent me)//long press to return to home page
        {
            if (!buttonorGesture){
                vib.vibrate(50);
                finish();
            }
        }
    }
}
