package com.example.tomek.shoutbox.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tomek.shoutbox.MojObrazek;
import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.adapters.AdapterMojeObrazki;
import com.example.tomek.shoutbox.utils.Typy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MojeObrazki extends XstActivity implements AdapterView.OnItemClickListener {

    private AdapterMojeObrazki adapterMojeObrazki;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackButtonInActionBar();
        setContentView(R.layout.activity_moje_obrazki);

        adapterMojeObrazki = new AdapterMojeObrazki(this);

        progressBar = findViewById(R.id.obrazkiProgressBar);
        GridView grid = findViewById(R.id.gridObrazki);
        grid.setOnItemClickListener(this);
        grid.setAdapter(adapterMojeObrazki);
        getObrazki();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getObrazki();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapterMojeObrazki != null) {
            if (adapterMojeObrazki.getCount() >= position) {
                MojObrazek obrazek = (MojObrazek) adapterMojeObrazki.getItem(position);
                Intent l_intent = new Intent(this, PokazObrazekActivity.class);
                l_intent.putExtra("image_url", obrazek.getObrazekUrl());
                startActivity(l_intent);
            }
        }
    }

    private void getObrazki() {
        pokazProgressBar();
        HashMap<String, String> params = new HashMap<>();
        params.put("key", apiKey);
        JSONObject request = new JSONObject(params);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Typy.API_GET_OBRAZKI, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                schowajProgressBar();
                try {
                    int success = response.getInt("success");
                    if (success == 1) {
                        JSONArray obrazki = response.getJSONArray("images");
                        adapterMojeObrazki.setObrazki(obrazki);
                    }
                } catch (JSONException ignored) {
                    Toast.makeText(getApplicationContext(), "Błąd po stronie serwera. Kod: OB1", Toast.LENGTH_SHORT).show();
                }
            }
        }, new VolleyErrorListener());
        req.setTag(Typy.TAG_GET_MSG);
        getRequestQueue().add(req);
    }

    private void schowajProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void pokazProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private class VolleyErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            schowajProgressBar();
            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                // błąd internetu
                Toast.makeText(getApplicationContext(), "Sprawdź połączenie z internetem", Toast.LENGTH_SHORT).show();
            } else if (error instanceof AuthFailureError) {
                Toast.makeText(getApplicationContext(), "AuthFailureError", Toast.LENGTH_SHORT).show();
            } else if (error instanceof ServerError) {
                Toast.makeText(getApplicationContext(), "ServerError", Toast.LENGTH_SHORT).show();
            } else if (error instanceof NetworkError) {
                Toast.makeText(getApplicationContext(), "NetworkError", Toast.LENGTH_SHORT).show();
            } else if (error instanceof ParseError) {
                Toast.makeText(getApplicationContext(), "ParseError", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
