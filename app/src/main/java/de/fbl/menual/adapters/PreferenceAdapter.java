package de.fbl.menual.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.fbl.menual.R;
import de.fbl.menual.utils.Config;
import de.fbl.menual.utils.Constants;
import it.beppi.tristatetogglebutton_library.TriStateToggleButton;

public class PreferenceAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> list = new ArrayList<String>();
    private Context context;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;


    public PreferenceAdapter(ArrayList<String> list, Context context, SharedPreferences sharedPref, SharedPreferences.Editor editor) {
        this.list = list;
        this.context = context;
        this.sharedPref = sharedPref;
        this.editor = editor;
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
        TextView listItemText = (TextView) view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        final TriStateToggleButton prefSwitch = (TriStateToggleButton) view.findViewById(R.id.preference_switch);


        prefSwitch.setOnToggleChanged(new TriStateToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(TriStateToggleButton.ToggleStatus toggleStatus, boolean booleanToggleStatus, int toggleIntValue) {
                editor.putInt(Constants.prefArray[position], toggleIntValue);
                editor.apply();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do your switch stuff
                prefSwitch.setToggleStatus(sharedPref.getInt(Constants.prefArray[position], 1));
                notifyDataSetChanged();
            }
        }, 1000);
        return view;
    }


}