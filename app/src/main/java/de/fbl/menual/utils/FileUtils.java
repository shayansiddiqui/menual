package de.fbl.menual.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.fbl.menual.MainActivity;
import de.fbl.menual.R;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static android.support.constraint.Constraints.TAG;

public class FileUtils {

    public static boolean isCachedFileAvailable(Context context, String menuFile) {
        final File cachedJsonResponse = new File(context.getFilesDir(), menuFile);
        return cachedJsonResponse.exists();
    }

    public static File createCacheFile(Context context, String menuToUse) {
        final File cachedJsonResponse = new File(context.getFilesDir(), menuToUse);
        try {
            if (!cachedJsonResponse.createNewFile()) {
                Log.d("Menual", "failed to create directory");
                throw new IOException("Cannot create the preview file");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cachedJsonResponse;
    }

    public static byte[] readCachedImage(Context context, String cachedImageFile) {
        try {
            InputStream is =context.getAssets().open(cachedImageFile);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            return  buffer;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonObject readCachedResponseFile(Context context, String menu) throws IllegalStateException {
        try {
            String cachedResponseJson = readFile(context, menu);
            if (cachedResponseJson.isEmpty()) {
                throw new IllegalStateException();
            }
            JsonElement element = new JsonParser().parse(cachedResponseJson);
            return element.getAsJsonObject();
        } catch (Exception ex) {
            throw new IllegalStateException("Cached file contents are not readable");
        }
    }

    public static String readFile(Context context, String fileName) {
        StringBuilder fileContent = new StringBuilder();
        byte[] buffer = new byte[1024];
        int n;
        try {
            FileInputStream fis = context.openFileInput(fileName);
            while ((n = fis.read(buffer)) != -1) {
                fileContent.append(new String(buffer, 0, n));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent.toString();
    }

    public static void writePreviewResponseFile(Context context, byte[] content) {
        writeFile(context, Config.PREVIEW_RESPONSE_FILE_NAME, content);
    }

    public static void writeCacheResponseFile(Context context, byte[] content) {
        writeFile(context, Config.CACHED_MENU_TO_USE + ".json", content);
    }

    public static void writeFile(Context context, String filename, byte[] content) {
        File file = new File(context.getFilesDir(), filename);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    Log.d("Menual", "failed to create the preview file");
                    throw new IOException("Cannot create the preview file");

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(content);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap bytesToBitmap(byte[] data) {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, bounds);

        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static File createPreviewImageFile(Context context, Bitmap rotatedBitmap, byte[] data) {
        final File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions: ");
            return null;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        return pictureFile;
    }

    /**
     * Create a File for saving an image or video
     */
    public static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Menual");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Menual", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static String convertToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

}
