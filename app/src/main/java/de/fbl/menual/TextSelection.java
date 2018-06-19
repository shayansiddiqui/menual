package de.fbl.menual;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.fbl.menual.utils.Constants;

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
}
