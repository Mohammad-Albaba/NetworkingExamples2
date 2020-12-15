package com.example.networkingexamples2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
///****    VOLLEY  ***///
public class MainActivity extends AppCompatActivity {

    private RecyclerView photoRecyclerView;
    private List<Photo> photos;
    private PhotosAdapter photosAdapter;
    private NetworkUtils networkUtils;
    private Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        networkUtils = NetworkUtils.getInstance(this);
        gson = new Gson();
        photos = new ArrayList<>();
        photoRecyclerView = findViewById(R.id.recycler);
        photoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        photosAdapter = new PhotosAdapter(photos, new PhotosAdapter.OnLoadMoreLister() {
            @Override
            public void onLoadMore(int page) {
                loadData(page);
            }
        });
        photoRecyclerView.setAdapter(photosAdapter);
        loadData(0);
    }

    public void loadData(int page) {
        String url = networkUtils.getDataUrl(page);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Photos Response: " + response);
                Type photoListType = new TypeToken<List<Photo>>() {}.getType();
                try {
                    List<Photo> tempList = gson.fromJson(response, photoListType);
                    if (tempList != null){
                        if (tempList.size() == 0){
                            photosAdapter.setLastPage(true);
                        }else {
                            photos.addAll(tempList);
                            photosAdapter.notifyDataSetChanged();
                        }
                        photosAdapter.setLoading(false);
                    }
                }catch (JsonSyntaxException e){
                        ErrorResponse errorResponse = gson.fromJson(response, ErrorResponse.class);
                        Snackbar.make(findViewById(android.R.id.content) , errorResponse.getMessage(),Snackbar.LENGTH_LONG).show();
                }
                }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(findViewById(android.R.id.content) , networkUtils.parseVolleyErrors(error),Snackbar.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put(NetworkUtils.PARAM_TOKEN,Data.getToken());
                return params;
            }
        };
        networkUtils.addToRequestQueue(request);

    }
}



