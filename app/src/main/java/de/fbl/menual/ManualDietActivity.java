package de.fbl.menual;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;

import java.util.ArrayList;
import java.util.Arrays;

import de.fbl.menual.adapters.PreferenceAdapter;
import de.fbl.menual.utils.Constants;
import it.beppi.tristatetogglebutton_library.TriStateToggleButton;

/**
 * Display diet preferences for given user. Screen is used by the user to input his/her preferences
 * which will then be used in the nutritional algorithm
 */
public class ManualDietActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    ArrayList<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_preferences);
        list.addAll(Arrays.asList(Constants.prefArray));

        SharedPreferences sharedPreferences = getSharedPreferences(GoogleSignIn.getLastSignedInAccount(this).getEmail(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        //instantiate custom adapter
        PreferenceAdapter adapter = new PreferenceAdapter(list, this, sharedPreferences, editor);
        //handle listview and assign adapter

        ListView lView = (ListView) findViewById(R.id.preference_list);
        lView.setAdapter(adapter);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TriStateToggleButton triStateToggleButton = (TriStateToggleButton) view.findViewById(R.id.preference_switch);
                triStateToggleButton.toggle();
            }
        });



    }


}
