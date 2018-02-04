package com.example.tomek.xsttest1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by Tomek on 2017-10-26.
 */

public class FragmentLogowanie extends Fragment implements View.OnClickListener {
    View mView;
    Button mBtnZaloguj;
    EditText mInputLogin;
    EditText mInputHaslo;
    IMainActivity mImain;
    Activity mMain;

    public FragmentLogowanie() {

    }

    @Override
    public void onAttach(Context main) {
        super.onAttach(main);
        mImain = (IMainActivity) main;
        mMain = (LayoutGlownyActivity) main;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.login_layout, container, false);
        mInputHaslo = mView.findViewById(R.id.editTextPassword);
        mInputLogin = mView.findViewById(R.id.editTextLogin);
        mBtnZaloguj = mView.findViewById(R.id.btnZaloguj);

        mBtnZaloguj.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onPause() {
        super.onPause();
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
                            SharedPreferences.Editor editor = mMain.getSharedPreferences(Typy.PREFS_NAME, 0).edit();

                            JSONObject user = response.getJSONObject("user");
                            editor.putString(Typy.PREFS_API_KEY, user.getString("keyValue"));
                            editor.putString(Typy.PREFS_LOGIN, user.getString("username"));
                            editor.putString(Typy.PREFS_NICNKAME, user.getString("nickname"));
                            editor.putString(Typy.PREFS_AVATAR, user.getString("avatar"));
                            editor.commit();
                            mImain.zalogowano(false);
                        }
                        Toast.makeText(mMain, msg, Toast.LENGTH_SHORT).show();
                    } catch (JSONException ex) {
                        Log.i("xst", "PARSE ERROR: " + ex.getMessage());
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
                    Toast.makeText(mMain, message, Toast.LENGTH_SHORT).show();
                }
            });
            req.setTag(Typy.TAG_ZALOGUJ);
            mImain.getRequestQueue().add(req);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
