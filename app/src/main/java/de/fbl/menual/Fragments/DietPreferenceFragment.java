package de.fbl.menual.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import de.fbl.menual.MainActivity;
import de.fbl.menual.ManualDietActivity;
import de.fbl.menual.R;
import de.fbl.menual.SettingsActivity;

public class DietPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @SuppressLint("ResourceType")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.layout.diets);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        System.out.println(key);
        Preference selectedPref = this.findPreference(key);
        CheckBoxPreference gainPref = ((CheckBoxPreference)this.findPreference("checkboxPref_gain"));
        CheckBoxPreference losePref = ((CheckBoxPreference)this.findPreference("checkboxPref_lose"));
        CheckBoxPreference maintainPref = ((CheckBoxPreference)this.findPreference("checkboxPref_maintain"));
        if(key.equals("checkboxPref_gain")){
            //gainPref.setChecked(true);
            losePref.setChecked(false);
            maintainPref.setChecked(false);
        }else if(key.equals("checkboxPref_lose")){
           // losePref.setChecked(true);
            maintainPref.setChecked(false);
            gainPref.setChecked(false);
        } else if(key.equals("checkboxPref_maintain"))
        {
            //maintainPref.setChecked(true);
            losePref.setChecked(false);
            gainPref.setChecked(false);
        }

    }
}
