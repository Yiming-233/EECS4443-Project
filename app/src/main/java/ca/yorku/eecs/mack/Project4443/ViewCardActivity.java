package ca.yorku.eecs.mack.Project4443;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;


public class ViewCardActivity extends Activity implements SearchView.OnQueryTextListener{

    private ListView listView;
    private SearchView searchView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewcard);

        listView = findViewById(R.id.list_view);
        searchView = findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(this);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, MainActivity.words);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click event
                Intent i = new Intent(getApplicationContext(), FlashCardActivity.class);
                Bundle b = getIntent().getExtras();
                //retrieve selected item an view mode
                i.putExtra("index",position);
                i.putExtra(MainActivity.VIEW_MODE,b.getBoolean(MainActivity.VIEW_MODE,false));
                startActivity(i);
                //return value
                finish();
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        //Auto-fill
        ArrayList<String> results = new ArrayList<>();
        ArrayList<String>  word = MainActivity.words;

        for (int idx = 0; idx < word.size(); idx++) {
            if (word.get(idx).toLowerCase().contains(s.toLowerCase())) {
                results.add(word.get(idx));
            }
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, results);
        listView.setAdapter(adapter);
        return true;
    }
}
