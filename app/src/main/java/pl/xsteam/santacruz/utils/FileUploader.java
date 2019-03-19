package pl.xsteam.santacruz.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

/**
 * Uploading the file to server
 * */
public class FileUploader extends AsyncTask<Void, Integer, String> {
    private String filePath;
    private String key;
    private ProgressChanged listener;

    public FileUploader(String file, String key, ProgressChanged progressListener) {
        this.filePath = file;
        this.key = key;
        this.listener = progressListener;
    }

    @Override
    protected void onPreExecute() {
        // setting progress bar to zero
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        // Making progress bar visible
        if (listener != null) {
            listener.progressChanged(progress[0]);
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        return uploadFile();
    }

    private String uploadFile() {
        HttpResponse httpResponse = null;
        HttpEntity httpEntity = null;
        HttpClient httpClient = null;
        String responseString = null;
        HttpPost httpPost = null;

        try {
            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(Typy.URL_UPLOAD);

            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            multipartEntityBuilder.addTextBody("key", key); // new FormBodyPart("key", new StringBody(key, ContentType.TEXT_PLAIN)));
            multipartEntityBuilder.addPart("file", new FileBody(new File(this.filePath)));
            MyHttpEntity.ProgressListener progressListener =
                    new MyHttpEntity.ProgressListener() {
                        @Override
                        public void transferred(float progress) {
                            publishProgress((int) progress);
                        }
                    };
            // POST
            MyHttpEntity myEntity = new MyHttpEntity(multipartEntityBuilder.build(), progressListener);
            Log.i("xst", "wysylam key: " + key);
            httpPost.setEntity(myEntity);
            httpResponse = httpClient.execute(httpPost);
            httpEntity = httpResponse.getEntity();

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(httpEntity);
            } else {
                responseString = "Error occurred! Http Status Code: "
                        + statusCode;
            }
        } catch (IOException e) {
            responseString = e.toString();
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i("xst", "Response from server: " + result);
        super.onPostExecute(result);
        listener.imageUploaded(result);
    }

    public void setProgressChangedListener(ProgressChanged listener) {
        this.listener = listener;
    }

    public interface ProgressChanged {
        public void progressChanged(int progress);
        public void imageUploaded(String response);
    }

}