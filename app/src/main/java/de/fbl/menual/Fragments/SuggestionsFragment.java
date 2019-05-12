package de.fbl.menual.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import de.fbl.menual.R;
import de.fbl.menual.StatisticsActivity;
import de.fbl.menual.TextSelectionActivity;
import de.fbl.menual.adapters.MealSuggestionsAdapter;
import de.fbl.menual.utils.Constants;
import de.fbl.menual.utils.Restaurant;
import de.fbl.menual.utils.RestaurantMocks;

/**
 * A simple {@link Fragment} subclass.
 */
public class SuggestionsFragment extends Fragment {


    public SuggestionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.suggestions_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView suggestionList = (ListView) getView().findViewById(R.id.suggestsion_list);
        final ArrayList<Restaurant> restaurants = RestaurantMocks.getRestaurants();

        suggestionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent myIntent = new Intent(getContext(), TextSelectionActivity.class);
                myIntent.putExtra(Constants.SEARCH_QUERY, "Pork Knuckle"); //Optional parameters
//                myIntent.putExtra(Constants.DETECTION_RESPONSE_KEY, Config.PREVIEW_RESPONSE_FILE_NAME); //Optional parameters
//                myIntent.putExtra(Constants.MEAL_TYPE_KEY, getMealType());
                getContext().startActivity(myIntent);
//                showResultAlert(foodItems.get(i));
            }
        });


        MealSuggestionsAdapter mAdaptor = new MealSuggestionsAdapter(restaurants, getContext());
        suggestionList.setAdapter(mAdaptor);
    }
}

