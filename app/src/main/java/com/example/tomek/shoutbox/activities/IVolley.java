package com.example.tomek.shoutbox.activities;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

public interface IVolley {
    ImageLoader getImageLoader();
    RequestQueue getRequestQueue();
}
