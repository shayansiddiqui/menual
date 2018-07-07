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
import it.beppi.tristatetogglebutton_library.TriStateToggleButton;
/**
public class PreferenceAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> list = new ArrayList<String>();
    private Context context;


    public PreferenceAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.preference, null);
        }

        //Handle TextView and display string from your list
       // TextView listItemText = (TextView) view.findViewById(R.id.list_item_string);
       // listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
       // TriStateToggleButton prefSwitch = (TriStateToggleButton) view.findViewById(R.id.preference_switch);
/**
        prefSwitch.setOnToggleChanged(new TriStateToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(TriStateToggleButton.ToggleStatus toggleStatus, boolean booleanToggleStatus, int toggleIntValue) {
                switch (toggleStatus) {
                    case off:
                        break;
                    case mid:
                        break;
                    case on:
                        break;
                }
            }
        });

        return view;
    }


}
*/