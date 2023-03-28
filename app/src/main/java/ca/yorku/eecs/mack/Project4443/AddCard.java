package ca.yorku.eecs.mack.Project4443;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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

        Intent i = new Intent();
        i.putExtra("word",newWord);
        i.putExtra("def",newDef);
        setResult(Activity.RESULT_OK, i);
        finish();
    }
}
