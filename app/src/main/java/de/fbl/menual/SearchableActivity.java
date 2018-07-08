package de.fbl.menual;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AppCompatActivity;

import de.fbl.menual.utils.Constants;
import de.fbl.menual.utils.SuggestionsProvider;

public class SearchableActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // Get the intent, verify the action and get the query
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SuggestionsProvider.AUTHORITY, SuggestionsProvider.MODE);
            suggestions.saveRecentQuery(query, null);

            Intent myIntent = new Intent(getApplicationContext(), TextSelectionActivity.class);
            myIntent.putExtra(Constants.SEARCH_QUERY, query);
            getApplicationContext().startActivity(myIntent);
        }
    }
}
