package de.fbl.menual;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.fbl.menual.utils.Constants;
import de.fbl.menual.utils.NutritionUtils;

public class TextSelection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_selection);
        Bundle extras = getIntent().getExtras();
        File previewImage = (File) extras.get(Constants.PREVIEW_IMAGE_KEY);
        ImageView previewImageView = findViewById(R.id.preview_image);
//        previewImageView.setAdjustViewBounds(true);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bitmap = null;

        try{
            bitmap = BitmapFactory.decodeStream(new FileInputStream(previewImage), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(bitmap!=null) {
            previewImageView.setImageBitmap(bitmap);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.text_selection_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle save and discard button
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_food) {

            class ActionThread extends Thread {

                public void run(){
                    try {
                        Intent myIntent = new Intent(getApplicationContext(), ScanHistory.class);
                        NutritionUtils utils = new NutritionUtils();

                        //TODO: This is a mockup. Call function with actual information from pic to text api
                        utils.initiateApi("Big mac");
                        int listSize = utils.getList().size();
                        if(listSize == 0) {
                            Thread.sleep(2000);
                        }

                        myIntent.putExtra("populated-list", utils.getList());
                        getApplicationContext().startActivity(myIntent);

                    } catch (InterruptedException ex) {
                        // code to resume or terminate...
                    }
                }
            }

            Thread th = new ActionThread();
            th.start();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
