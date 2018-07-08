package de.fbl.menual.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.fbl.menual.R;
import de.fbl.menual.models.FoodItem;

public class MealSuggestionsAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> list = new ArrayList<String>();
    private Context context;

    public MealSuggestionsAdapter(ArrayList<String> list, Context context) {
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

        //Handle TextView and display string from your list
/*
        TextView listItemText = (TextView) view.findViewById(R.id.history_list);
        FoodItem foodItem = list.get(position);
        listItemText.setText(list.get(position));
        int color = R.color.yellow;
        int background = R.drawable.yellow;

        listItemText.setText(foodItem.getFoodName());
        String result = foodItem.getResult();
        switch (result) {
            case "green":
                background = R.drawable.green;
                color = R.color.green;
                break;
            case "red":
                background = R.drawable.red;
                color = R.color.red;
                break;
            case "yellow":
                background = R.drawable.yellow;
                color = R.color.yellow;
                break;
        }
        ImageView listItemIcon = (ImageView) view.findViewById(R.id.food_item_result_icon);
        listItemText.setTextColor(context.getResources().getColor(color));
        listItemIcon.setBackgroundResource(background);
*/


        return view;

    }
}
