package pl.xsteam.santacruz.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class ImagesFromGallery extends AsyncTask<Context, Integer, String> {
    private ImageFoundListener listener;
    private ArrayList<String> lista;

    public ImagesFromGallery(ImageFoundListener progressListener) {
        lista = new ArrayList<>();
        listener = progressListener;
    }

    @Override
    protected void onPreExecute() {
        // setting progress bar to zero
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {

    }

    @Override
    protected String doInBackground(Context... params) {
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, //the album it in
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        ContentResolver resolver = params[0].getContentResolver();
        if (resolver != null) {
            final Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                 projection,
                                                 null,
                                                 null,
                                                 MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

            if (cursor == null) {
                listener.noImages();
                return "";
            }

            if (cursor.moveToFirst()) {
                do {
                    String imageLocation = cursor.getString(1);
                    File imageFile = new File(imageLocation);
                    if (imageFile.exists()) {
                        lista.add(imageLocation);
                    }
                } while(cursor.moveToNext());
            }
            cursor.close();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        listener.imagesFound(lista);
        Log.i("xst", "ImagesFromGallery: wczytano obrazkow: " + lista.size());
    }

    public interface ImageFoundListener {
        void imagesFound(ArrayList<String> lista);
        void noImages();
    }
}