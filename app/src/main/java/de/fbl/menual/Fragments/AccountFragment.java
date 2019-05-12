package de.fbl.menual.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import de.fbl.menual.R;

/**
 * Fragment containing account specific settings
 */
public class AccountFragment extends PreferenceFragment {
    @SuppressLint("ResourceType")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.layout.account_settings);
    }
}
