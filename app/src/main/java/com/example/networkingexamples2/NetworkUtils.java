package com.example.networkingexamples2;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.LruCache;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

    ///****    VOLLEY  ***///
public class NetworkUtils {
    private final String BASE_URL = "https://omaralbelbaisy.com/api/";
    private final String LOGIN_BATH = "login.php";
    private final String DATA_BATH = "data.php";

    public static final String PARAM_USERNAME = "username" ;
    public static final String PARAM_PASSWORD = "password" ;
    public static final String PARAM_TOKEN = "Token" ;
    private final String PARAM_PAGE = "page" ;

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private Context context;

    private static NetworkUtils instance;

    public static NetworkUtils getInstance(Context context){
        if (instance == null){
            instance = new NetworkUtils(context.getApplicationContext());
        }
        return instance;
    }

    private NetworkUtils(Context context){
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        ImageLoader imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private LruCache<String, Bitmap> cache = new LruCache<>(20000000);
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });

    }
    public String getLoginUrl() {
        return Uri.parse(BASE_URL + LOGIN_BATH).buildUpon().build().toString();
    }

    public String getDataUrl(int page) {
        return Uri.parse(BASE_URL + DATA_BATH).buildUpon()
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page)).build().toString();
    }
    public void addToRequestQueue(Request request){
        request.setTag("request");
        requestQueue.add(request);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
    public void cancelRequests(){
        requestQueue.cancelAll("request");
    }
    public String parseVolleyErrors(VolleyError volleyError) {
        if (volleyError != null ) {
            NetworkResponse networkResponse = volleyError.networkResponse;
            if (networkResponse != null){
                System.out.println("Status Code: " + networkResponse.statusCode);
            }
            if (volleyError instanceof NoConnectionError){
                return context.getString(R.string.error_no_connection);
            }else if (volleyError instanceof NetworkError){
                return context.getString(R.string.error_no_network);
            }else if (volleyError instanceof AuthFailureError){
                return context.getString(R.string.error_auth_fail);
            }else if (volleyError instanceof TimeoutError){
                return context.getString(R.string.error_timeout);
            }else if (volleyError instanceof ServerError){
                return context.getString(R.string.error_server);
            }else{
                return context.getResources().getString(R.string.general_error);
            }
        } else {
            return context.getResources().getString(R.string.general_error);
        }
    }
}