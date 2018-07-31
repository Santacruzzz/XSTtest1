package com.example.tomek.shoutbox.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.tomek.shoutbox.R;

public class UploadActivity extends XstActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackButtonInActionBar();
        setContentView(R.layout.activity_upload);

        Intent openIntent = getIntent();

        if (openIntent != null) {
            if (openIntent.getExtras() != null) {
                Bundle extra = openIntent.getExtras();
                Uri myUri = Uri.parse(extra.getString("imageUri"));
                ImageView imageUpload = findViewById(R.id.imageUpload);
                imageUpload.setImageURI(myUri);
            }
        }
    }
}
