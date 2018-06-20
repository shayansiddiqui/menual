package de.fbl.menual;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

import de.fbl.menual.adapters.PreferenceAdapter;

public class ScanHistory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_preferences);

        //generate list
        ArrayList<String> list = new ArrayList<String>();

        list = getIntent().getStringArrayListExtra("populated-list");

        //instantiate custom adapter
        PreferenceAdapter adapter = new PreferenceAdapter(list, this);

        //handle listview and assign adapter
        ListView lView = (ListView) findViewById(R.id.preference_list);
        lView.setAdapter(adapter);
    }

    public void populateHistoryList(String foodName, int[] scores, boolean detailed)
    {
        ArrayList<String> list = new ArrayList<String>();
        if(detailed) {
            if (scores[0] > 100) {
                System.out.println("green");


            } else {
                if (scores[0] > 90)
                    System.out.println("yellow");
                else
                    System.out.println("red");
            }
        } else {
            // for(int i = 0 ; i < scores.length; i++){

            // }
            //list.add(foodName);
            System.out.println(list.toString());
        }
    }
}
