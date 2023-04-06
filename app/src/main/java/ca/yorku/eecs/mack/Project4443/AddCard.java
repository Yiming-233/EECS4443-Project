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
    Button addB,backB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.makecard);
        word = (EditText)findViewById(R.id.wordText);
        def = (EditText)findViewById(R.id.defText);
        addB = (Button)findViewById(R.id.addButton);
        backB = (Button)findViewById(R.id.back);
        addB.setOnClickListener(this);
        backB.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if(v == addB){//if user finish adding card
            String newWord = String.valueOf(word.getText());
            String newDef = String.valueOf(def.getText());

            if(!newWord.isEmpty() && !newDef.isEmpty()){
                MainActivity.words.add(newWord);
                MainActivity.defs.add(newDef);
                String s = newWord + "#"+newDef;
                addCard(s);
            }
            else
                Toast.makeText(AddCard.this, "Your word or definition is empty", Toast.LENGTH_LONG).show();

        }
        else
            finish();
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
