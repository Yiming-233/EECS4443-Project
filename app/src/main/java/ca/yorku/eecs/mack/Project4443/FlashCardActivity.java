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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashcard);
        toastBeforeExit = true; // the user gets one reminder before exiting app

        // UI responds to touch gestures
        cardView = (FrameLayout)findViewById(R.id.container);
        cardView.setOnTouchListener(this);
        gestureDetector = new GestureDetector(this.getBaseContext(), new MyGestureListener());


        // init vibrator (used for long-press gesture)
        vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        if (savedInstanceState == null) // the activity is being created for the 1st time
        {
            // initialize SharedPreferences instance and load the settings
            sp = PreferenceManager.getDefaultSharedPreferences(this);
            Bundle b = getIntent().getExtras();
            cardIdx = b.getInt("index", 0);
            // put the fragments UIs in the activity's content
            getFragmentManager().beginTransaction().add(R.id.container, new WordFragment()).commit();
        }
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
        // launch the SettingsActivity to allow the user to change the app's settings
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

    private void nextWord() {
        if (!MainActivity.words.isEmpty())
        if(cardIdx < MainActivity.words.size() -1)
            cardIdx++;
        else
            cardIdx = 0;
        showingBack = false;
        getFragmentManager().beginTransaction().setCustomAnimations(R.animator.view_appear_enter,
                        R.animator.view_appear_exit).replace(R.id.container, new WordFragment()).addToBackStack(null)
                .commit();
    }

    private void removeCard(){
        MainActivity.words.remove(cardIdx);
        MainActivity.defs.remove(cardIdx);
        MainActivity.backup.remove(cardIdx);
        cardIdx--;
        File file = MainActivity.file;
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("file Deleted");
                try {
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
        if(MainActivity.words.isEmpty())
            finish();
        else
            nextWord();
    }
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent me) {
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
            vib.vibrate(50);
            finish();
        }
    }
}
