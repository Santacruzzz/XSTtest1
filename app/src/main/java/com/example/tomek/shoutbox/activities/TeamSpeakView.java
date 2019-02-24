package com.example.tomek.shoutbox.activities;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.tomek.shoutbox.R;

public class TeamSpeakView extends XstActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_speak_view);

        ustawToolbar();
        enableBackButtonInActionBar();
        final WebView webView = findViewById(R.id.webView);
        webView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int height = webView.getHeight();
//                String text = "l=%S r=%S bottom=%S top=%S";
//                Toast.makeText(getApplicationContext(), String.format(text, left, right, bottom, top), Toast.LENGTH_LONG).show();
                webView.loadUrl("https://net-speak.pl/preview2.php?ip=178.217.190.49&port=6450&kolor=1&width=&height=" + height / 2.708);
            }
        });
    }

    private int dpToPx(int dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }
}
