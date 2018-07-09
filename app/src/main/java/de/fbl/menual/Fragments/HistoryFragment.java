package de.fbl.menual.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import de.fbl.menual.R;
import de.fbl.menual.adapters.MealSuggestionsAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {


    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.history_fragment, container, false);

        //TextView tv = (TextView) v.findViewById(R.id.history_tab);
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
        //CustomAdapter arrayAdapter = new CustomAdapter(getActivity(), R.layout.search_catagory_list, prgmImages, prgmNameList);
        ListView lv = (ListView) v.findViewById(R.id.history_tab);
        MealSuggestionsAdapter mAdaptor = new MealSuggestionsAdapter(list, getContext());
        lv.setAdapter(mAdaptor);
        return v;
        //return inflater.inflate(R.layout.history_fragment, container, false);
    }

}

