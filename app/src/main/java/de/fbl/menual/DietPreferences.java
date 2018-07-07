package de.fbl.menual;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

import de.fbl.menual.adapters.PreferenceAdapter;

public class DietPreferences extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_preferences);

        //generate list
        ArrayList<String> list = new ArrayList<String>();
        list.add("High fat");
        list.add("High protien");
        list.add("High carbohydrates");
        list.add("High sugar");
        list.add("Gluten");
        list.add("Nuts");
        list.add("Milk");
        list.add("Eggs");
        list.add("Meat");
        list.add("Fish");

        //instantiate custom adapter
        PreferenceAdapter adapter = new PreferenceAdapter(list, this);

        //handle listview and assign adapter
        ListView lView = (ListView) findViewById(R.id.preference_list);
        lView.setAdapter(adapter);
    }

}
