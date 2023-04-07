package ca.yorku.eecs.mack.Project4443;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileWriter;

public class AddCard extends Activity implements View.OnClickListener {
    EditText word;
    EditText def;
    Button addB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.makecard);
        word = (EditText)findViewById(R.id.wordText);
        def = (EditText)findViewById(R.id.defText);
        addB = (Button)findViewById(R.id.addButton);
        addB.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        String newWord = String.valueOf(word.getText());
        String newDef = String.valueOf(def.getText());

        if(!newWord.isEmpty() && !newDef.isEmpty()){
            String s = newWord + "#"+newDef;
            MainActivity.words.add(newWord);
            MainActivity.defs.add(newDef);
            MainActivity.backup.add(s);
            addCard(s);
        }
        else
            Toast.makeText(AddCard.this, "Your word or definition is empty", Toast.LENGTH_LONG).show();

    }

    private void addCard(String newWord){
        if (newWord != "") {
            try {
                FileWriter writer = new FileWriter(MainActivity.file,true);
                writer.append(newWord);
                writer.append("\n");
                writer.flush();
                writer.close();
                Toast.makeText(AddCard.this, "Saved your flashcard", Toast.LENGTH_LONG).show();
                word.setText("");
                def.setText("");
            } catch (Exception e) {
            }
        }
    }
}
