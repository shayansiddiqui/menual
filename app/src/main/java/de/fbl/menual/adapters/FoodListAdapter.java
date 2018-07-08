package de.fbl.menual.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.fbl.menual.R;
import de.fbl.menual.models.FoodItem;

public class FoodListAdapter extends BaseAdapter implements ListAdapter {

    private List<FoodItem> list = new ArrayList<FoodItem>();
    private Context context;


    public FoodListAdapter(List<FoodItem> list, Context context) {
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
            view = inflater.inflate(R.layout.food_item, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView) view.findViewById(R.id.food_item_string);
        FoodItem foodItem = list.get(position);
        int color = R.color.yellow;
//        int background = R.drawable.yellow;

        listItemText.setText(foodItem.getFoodName());
        String result = foodItem.getResult();
        ImageView foodItemImage = (ImageView) view.findViewById(R.id.food_item_image);
        new ImageLoadTask(foodItem.getLowResPhoto(), foodItemImage).execute();

        switch (result) {
            case "green":
//                background = R.drawable.green;
                color = R.color.green;
                break;
            case "red":
//                background = R.drawable.red;
                color = R.color.red;
                break;
            case "yellow":
//                background = R.drawable.yellow;
                color = R.color.yellow;
                break;
        }
//        ImageView listItemIcon = (ImageView) view.findViewById(R.id.food_item_result_icon);
        listItemText.setTextColor(context.getResources().getColor(color));
//        listItemIcon.setBackgroundResource(background);


        return view;
    }

    private class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                if(url==null || url.isEmpty()){
                    url = "https://d2eawub7utcl6.cloudfront.net/images/nix-apple-grey.png";
                }

                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }

    }
}
