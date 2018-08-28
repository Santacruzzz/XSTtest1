package com.example.tomek.shoutbox.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.HashMap;

public class MojeObrazki extends XstActivity implements AdapterView.OnItemClickListener,
        AbsListView.MultiChoiceModeListener {

    private AdapterMojeObrazki adapterMojeObrazki;
    private ProgressBar progressBar;
    private GridView grid;
    private ArrayList<MojObrazek> zaznaczoneObrazki;
    private ActionMode actionMode;
    private int imagesMaxDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moje_obrazki);
        ustawToolbar();
        enableBackButtonInActionBar();

        zaznaczoneObrazki = new ArrayList<>();
        adapterMojeObrazki = new AdapterMojeObrazki(this);

        progressBar = findViewById(R.id.obrazkiProgressBar);
        grid = findViewById(R.id.gridObrazki);
        grid.setMultiChoiceModeListener(this);
        grid.setOnItemClickListener(this);
        grid.setAdapter(adapterMojeObrazki);
    }

    @Override
    protected void onResume() {
        super.onResume();
        downloadImagesList();
        Log.i("xst", "OBRAZKI RESUME");
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

    private void downloadImagesList() {
        pokazProgressBar();
        HashMap<String, String> params = new HashMap<>();
        params.put("key", apiKey);
        params.put("last_date", String.valueOf(obrazkiLastDate));
        JSONObject request = new JSONObject(params);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Typy.API_GET_OBRAZKI, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                schowajProgressBar();
                try {
                    int success = response.getInt("success");
                    if (success == 1) {
                        int max_date = response.getInt("max_date");
                        if (obrazkiLastDate < max_date) {
                            obrazkiLastDate = max_date;
                            // są nowe obrazki
                            JSONArray obrazki = response.getJSONArray("images");
                            Log.i("xst", "Pobralem: " + obrazki.length() + " NOWYCH obrazkow");
                            adapterMojeObrazki.dodajObrazki(obrazki);
                        }
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

    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean checked) {
        int items = grid.getCheckedItemCount();
        actionMode.setTitle(String.format("Wybrano: %d", items));
        adapterMojeObrazki.setItemCheckedState(i, checked);
        MojObrazek obrazek = (MojObrazek) adapterMojeObrazki.getItem(i);
        if (checked) {
            zaznaczoneObrazki.add(obrazek);
        } else {
            zaznaczoneObrazki.remove(obrazek);
        }
        actionMode.getMenu()
                  .findItem(R.id.menu_obrazki_kopiuj)
                  .setVisible(zaznaczoneObrazki.size() == 1);
        setObrazeItemState(i, checked);
    }

    @Override
    public boolean onCreateActionMode(ActionMode p_actionMode, Menu menu) {
        vibrate();
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_obrazki, menu);
        actionMode = p_actionMode;
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_obrazki_usun:
                pokazDialogUsuwania();
                return true;
            case R.id.menu_obrazki_kopiuj:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("xst", zaznaczoneObrazki.get(0).getObrazekUrl());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getApplicationContext(), "Skopiowano link", Toast.LENGTH_SHORT).show();
                }
                actionMode.finish();
                return true;
            default:
                return false;
        }
    }

    private void deleteCheckedImages() {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", apiKey);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < zaznaczoneObrazki.size(); i++) {
            jsonArray.put(zaznaczoneObrazki.get(i).id);
        }
        params.put("images", jsonArray.toString());

        JSONObject request = new JSONObject(params);
        pokazProgressBar();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Typy.API_DEL_OBRAZKI, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                schowajProgressBar();
                try {
                    int success = response.getInt("success");
                    if (success == 1) {
                        JSONArray usuniete = response.getJSONArray("deleted_images");
                        Toast.makeText(xstApp, "Usunieto", Toast.LENGTH_SHORT).show();
                        if (actionMode != null) {
                            actionMode.finish();
                        }
                        usunObrazki(usuniete);
                    } else {
                        String message = response.getString("message");
                        Toast.makeText(xstApp, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException ignored) {
                    Toast.makeText(getApplicationContext(), "Błąd po stronie serwera. Kod: OB2", Toast.LENGTH_SHORT).show();
                }
            }
        }, new VolleyErrorListener());
        req.setTag(Typy.TAG_GET_MSG);
        getRequestQueue().add(req);
    }

    private void usunObrazki(JSONArray usuniete) {
        for (int i = 0; i < usuniete.length(); i++) {
            try {
                usunObrazekId(usuniete.getInt(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (usuniete.length() > 0) {
            adapterMojeObrazki.notifyDataSetChanged();
        }
    }

    private void usunObrazekId(int id) {
        for (int i = 0; i < adapterMojeObrazki.getCount(); i++) {
            MojObrazek mojObrazek = (MojObrazek) adapterMojeObrazki.getItem(i);
            if (mojObrazek.id == id) {
                adapterMojeObrazki.deleteItem(i);
            }
        }
        adapterMojeObrazki.notifyDataSetChanged();
    }

    @Override
    public void onDestroyActionMode(ActionMode p_actionMode) {
        actionMode = null;
        grid.clearChoices();
        adapterMojeObrazki.clearChoices();
        zaznaczoneObrazki.clear();
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

    public void setObrazeItemState(int position, boolean state) {

        int firstVisiblePosition = grid.getFirstVisiblePosition();

        for (int k = 0; k < grid.getChildCount(); k++ ) {
            int current = firstVisiblePosition + k;

            if (current == position) {
                MojObrazek obrazek = (MojObrazek) adapterMojeObrazki.getItem(position);
                obrazek.setChecked(state);
                View row = grid.getChildAt(k);
                if (row == null) {
                    return;
                }
                if ((int) row.getTag() != obrazek.id) {
                    return;
                }
                ImageView checkOverlay = row.findViewById(R.id.imgOverlay);
                if (state) {
                    checkOverlay.setVisibility(View.VISIBLE);
                } else {
                    checkOverlay.setVisibility(View.INVISIBLE);
                }

            }
        }
    }

    private void pokazDialogUsuwania() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(MojeObrazki.this);
        builder.setMessage("Usunąć zaznaczone obrazki?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteCheckedImages();
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });
        builder.create();
        builder.show();
    }

    public void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Start without a delay
        // Vibrate for 100 milliseconds
        // Sleep for 1000 milliseconds
        long[] pattern = {0, 100, 1000};

        // The '0' here means to repeat indefinitely
        // '0' is actually the index at which the pattern keeps repeating from (the start)
        // To repeat the pattern from any other point, you could increase the index, e.g. '1'
        if (v != null) {
            v.vibrate(pattern, -1);
        }
    }
}
