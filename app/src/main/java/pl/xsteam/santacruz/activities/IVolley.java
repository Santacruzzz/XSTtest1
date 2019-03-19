package pl.xsteam.santacruz.activities;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

public interface IVolley {
    ImageLoader getImageLoader();
    RequestQueue getRequestQueue();
}
