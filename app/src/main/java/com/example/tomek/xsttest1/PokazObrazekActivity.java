package com.example.tomek.xsttest1;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PokazObrazekActivity extends Activity implements Callback {

    private ImageView photoView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokaz_obrazek);

        Bundle extras = getIntent().getExtras();
        String url = "";

        if (extras != null) {
            url = extras.getString("image_url");
        }

        photoView = findViewById(R.id.obrazek);
        progressBar = findViewById(R.id.progressBar);

        Picasso.with(this)
                .load(url)
                .into(photoView, this);
    }

    @Override
    public void onSuccess()
    {
        // hide the loader and show the imageview
        progressBar.setVisibility(View.GONE);
        photoView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError()
    {
        // hide the loader and show the imageview which shows the error icon already
        progressBar.setVisibility(View.GONE);
        photoView.setVisibility(View.VISIBLE);
    }
}