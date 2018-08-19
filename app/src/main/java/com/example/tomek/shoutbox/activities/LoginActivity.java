package com.example.tomek.shoutbox.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.utils.Typy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends XstActivity implements View.OnClickListener {

    Button mBtnZaloguj;
    EditText mInputLogin;
    EditText mInputHaslo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        mInputHaslo = findViewById(R.id.editTextPassword);
        mInputLogin = findViewById(R.id.editTextLogin);
        mBtnZaloguj = findViewById(R.id.btnZaloguj);
        mBtnZaloguj.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnZaloguj) {
            String url;
            url = Typy.API_ZALOGUJ;
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("login", mInputLogin.getText().toString());
            params.put("haslo", mInputHaslo.getText().toString());
            JSONObject request = new JSONObject(params);

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                @SuppressLint("ApplySharedPref")
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String msg = response.getString("message");
                        if (response.getInt("success") == 1) {
                            JSONObject user = response.getJSONObject("user");
                            xstApp.zapiszUstawienie(Typy.PREFS_API_KEY, user.getString("keyValue"));
                            xstApp.zapiszUstawienie(Typy.PREFS_LOGIN, user.getString("username"));
                            xstApp.zapiszUstawienie(Typy.PREFS_NICNKAME, user.getString("nickname"));
                            xstApp.zapiszUstawienie(Typy.PREFS_AVATAR, user.getString("avatar"));
                            Intent resultData = new Intent();
                            resultData.putExtra("msg", msg);
                            setResult(RESULT_OK, resultData);
                            finish();
                        } else {
                            Toast.makeText(xstApp, "Nieprawidłowe dane", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException ex) {
                        Log.e("xst", "Login PARSE ERROR: " + ex.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    String message = "";
                    if (volleyError instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection1!";
//                    startScheduleService();
                    } else if (volleyError instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                    } else if (volleyError instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection2.";
//                    startScheduleService();
                    }
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
            req.setTag(Typy.TAG_ZALOGUJ);
            Volley.newRequestQueue(getApplicationContext()).add(req);
        }
    }
}