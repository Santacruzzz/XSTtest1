package com.example.tomek.shoutbox.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.XstApplication;
import com.example.tomek.shoutbox.utils.FileFromBitmap;
import com.example.tomek.shoutbox.utils.FileUploader;
import com.example.tomek.shoutbox.utils.Typy;
import com.example.tomek.shoutbox.utils.Utils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class UploadActivity extends XstActivity
        implements FileUploader.ProgressChanged,
        FileFromBitmap.FileFromBitmapListener,
        Button.OnClickListener {

    private static final String TAG ="xst";
    private ProgressBar progressBar;
    private String filePath = null;
    private File fileToUpload = null;
    private String key = null;
    private TextView txtPercentage;
    private TextView textUprawnienia;
    private TextView textUploadError;
    private Button btnUpload;
    private Button btnLewo;
    private Button btnPrawo;
    private Button btnUploadOk;
    private float rotation;
    private FileUploader fileUploader;
    private ConstraintLayout layoutComplete;
    private ConstraintLayout layoutUpload;
    private ConstraintLayout layoutPrzyciskiWyslania;
    private ConstraintLayout layoutError;
    private boolean isImageSizeOk;
    private boolean isFileUploaded;
    private ImageView imageViewUpload;
    private Uri fileUri;
    private String receivedUrl;
    private Button btnCopy;
    private Bitmap bitmapToUpload;
    private boolean isFileLoaded;
    private boolean isReadyForUpload;
    private CheckBox checkInsertLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackButtonInActionBar();
        setContentView(R.layout.activity_upload);
        txtPercentage = findViewById(R.id.textPercent);
        textUprawnienia = findViewById(R.id.textUprawnienia);
        textUploadError = findViewById(R.id.textUploadError);
        btnUpload = findViewById(R.id.buttonUpload);
        btnLewo = findViewById(R.id.buttonObrocLewo);
        btnPrawo = findViewById(R.id.buttonObrocPrawo);
        btnUploadOk = findViewById(R.id.buttonCloseUpload);
        btnCopy = findViewById(R.id.buttonCopyToClipboard);
        progressBar = findViewById(R.id.progressBarUpload);
        progressBar.setVisibility(View.INVISIBLE);
        imageViewUpload = findViewById(R.id.imageUpload);
        key = ((XstApplication) getApplicationContext()).getApiKey();
        layoutComplete = findViewById(R.id.layoutComplete);
        layoutUpload = findViewById(R.id.layoutWyslij);
        layoutPrzyciskiWyslania = findViewById(R.id.layoutPrzyciskiWyslania);
        layoutError = findViewById(R.id.layoutError);
        checkInsertLink = findViewById(R.id.checkBoxInsertLink);
        rotation = 0;
        isFileUploaded = false;
        isFileLoaded = false;
        isReadyForUpload = false;

        Intent openIntent = getIntent();
        if (openIntent != null) {
            if (openIntent.getExtras() != null) {
                Bundle extra = openIntent.getExtras();
                fileUri = Uri.parse(extra.getString("imageUri"));
            }
        }
        btnUpload.setOnClickListener(this);
        btnLewo.setOnClickListener(this);
        btnPrawo.setOnClickListener(this);
        btnUploadOk.setOnClickListener(this);
        btnCopy.setOnClickListener(this);

        setReadyForUpload();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadFile() {
        if (filePath == null) {
            Toast.makeText(getApplicationContext(), "Nie mogę wczytać obrazka", Toast.LENGTH_LONG).show();
            return;
        }
        Log.i("xst", "start load file");
        bitmapToUpload = Utils.loadFileToBitmap(filePath);
        Log.i("xst", "loaded");

        isImageSizeOk = haveValidSize();
        Log.i("xst", "Rozmiar obrazka jest ok: " + String.valueOf(isImageSizeOk));
        if (!isImageSizeOk) {
            bitmapToUpload = Utils.scaleDown(bitmapToUpload, Typy.MAX_IMAGE_DIMENSION, true);
            FileFromBitmap fileCreator = new FileFromBitmap(bitmapToUpload, this);
            fileCreator.execute();
        } else {
            loadFileToImageView();
        }
    }

    private void loadFileToImageView() {
        isFileLoaded = true;
        Log.i("xst", "wczytuje do imageView: " + fileToUpload);
        Picasso.with(getApplicationContext())
                .load(fileToUpload)
                .error(R.drawable.jezyk)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageViewUpload);
    }

    private boolean haveValidSize() {
        return bitmapToUpload.getHeight() < Typy.MAX_IMAGE_DIMENSION && bitmapToUpload.getWidth() < Typy.MAX_IMAGE_DIMENSION;
    }

    private void startUploadProcedure() {
        if (bitmapToUpload == null) {
            Toast.makeText(getApplicationContext(), "Nie mogę wczytać obrazka", Toast.LENGTH_LONG).show();
            return;
        }
        setUploadingViewState();

        if (rotation != 0) {
            isReadyForUpload = true;
            Log.i("xst", "Rotacja != 0 zapisuję bitmapę");
            bitmapToUpload = ((BitmapDrawable) imageViewUpload.getDrawable()).getBitmap();
            FileFromBitmap fileCreator = new FileFromBitmap(bitmapToUpload, this);
            fileCreator.execute();
        } else {
            startUpload();
        }

    }

    private void setUploadingViewState() {
        layoutPrzyciskiWyslania.setVisibility(View.GONE);
    }

    private void setReadyForUpload() {

        if (filePath != null) {
            fileToUpload = new File(filePath);
        } else {
            filePath = Utils.getPath(getApplicationContext(), fileUri);
            fileToUpload = new File(filePath);
        }
        Log.i("xst", "FILE: " + filePath);

        if (!isFileUploaded) {
            if (!isFileLoaded) {
                loadFile();
            }
            if (!isImageSizeOk) {
                Toast.makeText(getApplicationContext(), "Obrazek jest za duży. Przed wysłaniem zostanie zmniejszony (max 2000 px)", Toast.LENGTH_LONG).show();
            }
        }

        layoutUpload.setVisibility(View.VISIBLE);
        layoutPrzyciskiWyslania.setVisibility(View.VISIBLE);
        layoutComplete.setVisibility(View.GONE);
        textUprawnienia.setVisibility(View.GONE);
    }

    @Override
    public void progressChanged(int progress) {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(progress);
        String percent = String.valueOf(progress) + "%";
        txtPercentage.setText(percent);
    }

    @Override
    public void imageUploaded(String response) {
        isFileUploaded = true;
        try {
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.getInt("success") == 1)
            {
                setUploadComplete();
                receivedUrl = jsonResponse.getString("url");
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setUploadError(response);

    }

    private void setUploadError(String response) {
        layoutComplete.setVisibility(View.GONE);
        layoutUpload.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        textUploadError.setText(response);
    }

    private void setUploadComplete() {
        isFileUploaded = true;
        layoutUpload.setVisibility(View.GONE);
        layoutComplete.setVisibility(View.VISIBLE);
    }

    // FileFromBitmap.FileFromBitmapListener
    @Override
    public void onOperationStart() {
//        /Toast.makeText(getApplicationContext(), "Przerabiam obrazek", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOperationDone(File file) {
        fileToUpload = file;
        filePath = file.getAbsolutePath();
        if (isReadyForUpload) {
            startUpload();
        } else {
            if (!isFileLoaded) {
                Log.i("xst", "file loaded!");
                loadFileToImageView();
            }
        }
    }

    private void startUpload() {
        Log.i("xst", "zaczynam upload");
        FileUploader uploader = new FileUploader(filePath, key, this);
        uploader.execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonUpload:
                startUploadProcedure();
                break;

            case R.id.buttonObrocLewo:
                rotateImage(-90);
                break;

            case R.id.buttonObrocPrawo:
                rotateImage(90);
                break;

            case R.id.buttonCloseUpload:
                Intent data = new Intent();
                String returnString = receivedUrl;
                if (checkInsertLink.isChecked()) {
                    returnString += "|true";
                } else {
                    returnString += "|false";
                }
                data.setData(Uri.parse(returnString));
                setResult(Activity.RESULT_OK, data);
                finish();
                break;

            case R.id.buttonCopyToClipboard:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("xst", receivedUrl);
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getApplicationContext(), "Skopiowano link", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    private void rotateImage(float angle) {
        if (!isFileLoaded) {
            return;
        }
        rotation += angle;
        if (Math.abs(rotation) == 360) {
            rotation = 0;
        }
        Picasso.with(getApplicationContext())
               .load(fileToUpload)
               .rotate(rotation)
               .memoryPolicy(MemoryPolicy.NO_CACHE)
               .into(imageViewUpload);
    }
}