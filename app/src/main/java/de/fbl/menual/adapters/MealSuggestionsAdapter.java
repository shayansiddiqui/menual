package de.fbl.menual.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.fbl.menual.R;
import de.fbl.menual.utils.Restaurant;

public class MealSuggestionsAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<Restaurant> list = new ArrayList<Restaurant>();
    private Context context;

    public MealSuggestionsAdapter(ArrayList<Restaurant> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.suggestions, null);
        }


        Restaurant restaurant = list.get(position);
        TextView restaurantName = (TextView) view.findViewById(R.id.restaurant_name);
        restaurantName.setText(restaurant.getName());

        TextView kitchenType = (TextView) view.findViewById(R.id.kitchen_type);
        kitchenType.setText(restaurant.getKitchenType());

        TextView restaurantLocation = (TextView) view.findViewById(R.id.restaurant_location);
        restaurantLocation.setText(restaurant.getLocation());

        return view;

    }
}
