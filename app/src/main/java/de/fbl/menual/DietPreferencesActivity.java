package de.fbl.menual;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import de.fbl.menual.Fragments.DietPreferenceFragment;

/**
 * Activity for diet preference selection for the user
 */
public class DietPreferencesActivity extends AppCompatActivity {

    /**
     * Shows the preference fragment
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_preferences);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new DietPreferenceFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DietPreferencesActivity.this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
