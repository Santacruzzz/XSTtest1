package com.example.tomek.shoutbox.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.utils.Utils;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PokazObrazekActivity extends XstActivity implements Callback {

    private ZoomageView photoView;
    private ProgressBar progressBar;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokaz_obrazek);

        ustawToolbar();
        enableBackButtonInActionBar();

        Bundle extras = getIntent().getExtras();
        url = "";

        if (extras != null) {
            url = extras.getString("image_url");
            String author = extras.getString("author");
            if (author == null) {
                author = nickname;
            }
            setTitle("Autor: " + author);
        }

        photoView = findViewById(R.id.obrazek);
        progressBar = findViewById(R.id.progressBar);
        Picasso.with(this)
                .load(url)
                .into(photoView, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_obrazki, menu);
        menu.getItem(1).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_obrazki_kopiuj:
                Utils.copyToClipboard(this, url, "Skopiowano link");
                break;
            case android.R.id.home:
                finish();
        }
        return true;
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
        Toast.makeText(xstApp,"Błąd sieci", Toast.LENGTH_SHORT).show();
    }
}