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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.fbl.menual.R;
import de.fbl.menual.StatisticsActivity;
import de.fbl.menual.TextSelectionActivity;
import de.fbl.menual.adapters.FoodListAdapter;
import de.fbl.menual.adapters.MealSuggestionsAdapter;
import de.fbl.menual.models.FoodItem;
import de.fbl.menual.utils.Constants;
import de.fbl.menual.utils.Restaurant;
import de.fbl.menual.utils.RestaurantMocks;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private ListView lv;
    List<FoodItem> foodItems = new ArrayList<>();

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        foodItems.add((FoodItem) bundle.get(Constants.FOOD_ITEM_KEY));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.history_fragment, container, false);
        lv = (ListView) v.findViewById(R.id.history_tab);
        FoodListAdapter adapter = new FoodListAdapter(foodItems, getContext());
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent myIntent = new Intent(getContext(), StatisticsActivity.class);
                myIntent.putExtra(Constants.FOOD_ITEM_KEY, foodItems.get(i));
                getContext().startActivity(myIntent);
            }
        });
        return v;
    }
}

