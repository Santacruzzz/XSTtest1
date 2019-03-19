package pl.xsteam.santacruz.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileFromBitmap extends AsyncTask<Void, Integer, String> {

    private FileFromBitmapListener listener;
    private File file;
    private Bitmap bitmap;
    private String path_external = Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg";

    public FileFromBitmap(Bitmap bitmap, FileFromBitmapListener p_listener) {
        this.bitmap = bitmap;
        this.listener = p_listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onOperationStart();
        Log.i("xst", "Tu FileFromBitmap, startuje");
    }

    @Override
    protected String doInBackground(Void... params) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        file = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
        try {
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.flush();
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.i("xst", "Operacja zapisyuwania skonczona, plik: " + file);
        listener.onOperationDone(file);
    }

    public interface FileFromBitmapListener
    {
        public void onOperationStart();
        public void onOperationDone(File file);
    }
}